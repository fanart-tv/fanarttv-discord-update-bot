/**
	The fanarttv-discord-update-bot makes GET requests to Fanart.TV, then parses and outputs the information to discord.
    Copyright (C) 2017  Michael Haas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tv.fanart;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.memetix.mst.language.Language;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HttpException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;
import tv.fanart.model.ActivityResponse;
import tv.fanart.model.ChangeResponse;
import tv.fanart.model.ChangeType;
import tv.fanart.model.ModifiedSection;
import tv.fanart.util.DateDeserializer;

/**
 * @author Michael Haas
 *
 */
public class FanartBot {
	/*
	 * SLF4J LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FanartBot.class);

	/*
	 * Sleep time between activity request updates
	 */
	private static final int SLEEP_TIME_SECONDS = 30;

	/*
	 * Microsoft translate API endpoint
	 */
	private static final String TRANSLATE_API_ENDPOINT = "http://api.microsofttranslator.com/V2/Http.svc/Translate";

	/*
	 * Discord API Client object
	 */
	private static JDA discordClient;

	/*
	 * Discord guild object
	 */
	private static Guild discordGuild;

	/*
	 * HTTP Client used for translation requests
	 */
	private static HttpClient client;

	/**
	 * @param args
	 *            The bot does not take arguments, and instead uses a configuration
	 *            file that generates on the first run.
	 */
	public static void main(String[] args) {
		// Initialize the update bot and run it in a new thread
		if (Configuration.getEnableUpdateBot())
			new Thread(new UpdateBotThread()).start();
		else
			LOGGER.info("The update bot is disabled, skipped initialization.");

		if (Configuration.getEnableTranslationBot()) {
			LOGGER.info("The translation bot is enabled, began initialization.");
			client = HttpClientBuilder.create().build();

			try {
				// Build the Discord client
				discordClient = new JDABuilder(AccountType.BOT).setAudioEnabled(false).setToken(Configuration.getDiscordAppClientSecret()).buildBlocking();
				LOGGER.info("Built Discord bot client for the translation bot");

				// Get the relevant Discord guild
				discordGuild = discordClient.getGuildById(Configuration.getDiscordGuildId());

				// Register the translation handler with the Discord client
				discordClient.addEventListener(new TranslatableMessageAdapter());
				LOGGER.info("Added the Discord bot client message listener");
			} catch (LoginException | IllegalArgumentException | RateLimitedException | InterruptedException e) {
				LOGGER.error("The Discord bot encountered an error.", e);
			}
		} else
			LOGGER.info("The translation bot is disabled, skipped initialization.");
	}

	public static class TranslatableMessageAdapter extends ListenerAdapter {
		/*
		 * HashMap of translation groups with a string representing the group name, then
		 * a list of channels in each. For example, you may see the key "general", and
		 * the value is an array list of the channels for "general," "general-de," and
		 * "general-fr."
		 */
		private HashMap<String, ArrayList<TranslationChannel>> translationGroups = new HashMap<>();

		/*
		 * HashMap of translation packages. The key is the string ID of a channel, with
		 * a translation package along with it. Keep in mind that a translation package
		 * has a source channel, then a list of destination channels.
		 */
		private HashMap<String, TranslationPackage> translationDestinations = new HashMap<>();

		public TranslatableMessageAdapter() {
			initializeChannelMap(discordGuild.getTextChannels());
			LOGGER.info("Initialized translation channel map.");
			initializeDestinationMap();
			LOGGER.info("Generated translation destination map.");
		}

		@Override
		public void onMessageReceived(MessageReceivedEvent event) {
			// Confirm that the channel is text, and that the channel is meant to be translated
			if (event.getChannelType().equals(ChannelType.TEXT) && translationDestinations.containsKey(event.getTextChannel().getId())) {
				TranslationPackage translationPackage = translationDestinations.get(event.getTextChannel().getId());

				TranslationChannel sourceChannel = translationPackage.getSourceChannel();
				List<TranslationChannel> destinations = translationPackage.getDestinationChannels();

				Message sourceMessage = event.getMessage();

				// Make sure that the message hasn't already been translated
				if (!(sourceMessage.isWebhookMessage() && sourceMessage.getAuthor().getName().matches(".*\\((Trans. |Translated )?(From )?[a-z]{2}\\)"))) {
					List<Attachment> sourceAttachments = sourceMessage.getAttachments();

					Attachment sourceAttachment = null;
					File temporaryFile = null;

					// Re-attach a file if there is one.
					if (sourceAttachments != null && !sourceAttachments.isEmpty() && sourceAttachments.get(0) != null) {
						sourceAttachment = sourceAttachments.get(0);
						temporaryFile = downloadMessageAttachment(sourceAttachment);
					}

					String avatarUrl = sourceMessage.getAuthor().getAvatarUrl();
					String authorName = sourceMessage.getAuthor().getName();

					List<MessageEmbed> embeds = sourceMessage.getEmbeds();

					// Get the destination author username (normally {OriginalUsername} ({SourceLanguage})
					String translatedAuthorUsername = getTranslatedAuthorUsername(authorName, sourceChannel.getLanguage().toString());

					// Translate and send each message for the destinations
					for (TranslationChannel currentChannel : destinations) {
						WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();

						if (avatarUrl != null)
							messageBuilder.setAvatarUrl(avatarUrl);

						if (translatedAuthorUsername != null)
							messageBuilder.setUsername(translatedAuthorUsername);

						String translatedMessage = translateMessage(sourceMessage.getContent(), sourceChannel.getLanguage(), currentChannel.getLanguage());
						if (translatedMessage != null)
							messageBuilder.setContent(translatedMessage);

						if (temporaryFile != null)
							messageBuilder.setFile(temporaryFile, sourceAttachment.getFileName());

						if (embeds != null && !embeds.isEmpty())
							messageBuilder.addEmbeds(embeds);

						// Build the webhook client for the destination channel
						WebhookClient webhookClient = new WebhookClientBuilder(currentChannel.getWebhook()).build();
						try {
							// Send the message
							webhookClient.send(messageBuilder.build());
							
							if (translatedMessage != null)
								LOGGER.info(String.format("Sent translated message (%s to %s) of length %d.", sourceChannel.getLanguage(), currentChannel.getLanguage(),
										translatedMessage.length()));
						} catch (IllegalStateException e) {
							// Log an error if the message building fails (typically due to a missing or errored field)
							LOGGER.error("Failed to send translated message.", e);
						}
						
						webhookClient.close();
					}

					// Delete the temporary file
					if (temporaryFile != null)
						temporaryFile.delete();
				}
			}
		}

		/**
		 * @param authorName
		 * @param string
		 * @return
		 */
		private String getTranslatedAuthorUsername(String authorName, String sourceLangCode) {
			if (authorName.length() <= 27)
				return String.format("%s (%s)", authorName, sourceLangCode);
			
			return authorName;
		}

		/**
		 * Download a Discord attachment file into a temporary file
		 * @param sourceAttachment attachment to download from 
		 * @return the temporary file that was downloaded to
		 */
		private File downloadMessageAttachment(Attachment sourceAttachment) {
			File temporaryFile = null;

			try {
				// Use the current millis time in the name to make sure it is always unique
				temporaryFile = File.createTempFile(sourceAttachment.getFileName() + "-" + System.currentTimeMillis(), ".discordfile");
				// Delete the file so that the discord client can download to it.
				temporaryFile.delete();
				if (!sourceAttachment.download(temporaryFile))
					temporaryFile = null;
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			return temporaryFile;
		}

		/**
		 * Translate a given string from one language to another
		 * @param content String to translate
		 * @param language Source language
		 * @param language2 Result language
		 * @return String of translated string
		 */
		private String translateMessage(String content, Language language, Language language2) {
			try {
				RequestBuilder request = RequestBuilder.get(TRANSLATE_API_ENDPOINT);
				request.addHeader("Ocp-Apim-Subscription-Key", Configuration.getMicrosoftTranslateKey());
				request.addParameter("text", content);
				request.addParameter("from", language.toString());
				request.addParameter("to", language2.toString());

				// This intentionally fails if the output does not match properly.
				String responseString = EntityUtils.toString(client.execute(request.build()).getEntity());
				try {
					return responseString.split("2003/10/Serialization/\">")[1].split("</string>")[0];
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("Failed to get translated text, response: ");
					System.out.println(responseString);
					throw new IOException(e);
				}

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		private void initializeDestinationMap() {
			translationGroups.forEach((groupName, groupChannels) -> {
				for (TranslationChannel outerChannel : groupChannels) {
					ArrayList<TranslationChannel> outerChannelDestinations = new ArrayList<>();
					for (TranslationChannel innerChannel : groupChannels)
						if (!innerChannel.equals(outerChannel))
							outerChannelDestinations.add(innerChannel);

					translationDestinations.put(outerChannel.getChannel().getId(), new TranslationPackage(outerChannel, outerChannelDestinations));
				}
			});
		}

		private void initializeChannelMap(List<TextChannel> channels) {
			for (TextChannel outerChannel : channels) {
				for (TextChannel innerChannel : channels) {
					if (innerChannel.getName().toLowerCase().matches(outerChannel.getName().toLowerCase() + "-[a-z]{2}")) {
						ArrayList<TranslationChannel> channelArray = translationGroups.get(outerChannel.getName().toLowerCase());
						if (channelArray == null) {
							channelArray = new ArrayList<TranslationChannel>();
							Webhook webhook = getValidWebhook(outerChannel);
							if (webhook != null)
								channelArray.add(new TranslationChannel(Language.ENGLISH, outerChannel, webhook));
						}

						TranslationChannel languageChannel = parseLanguageChannel(innerChannel);
						if (languageChannel != null)
							channelArray.add(languageChannel);

						if (channelArray != null)
							translationGroups.put(outerChannel.getName().toLowerCase(), channelArray);
					}
				}
			}
		}

		private TranslationChannel parseLanguageChannel(TextChannel innerChannel) {
			Webhook webhook = getValidWebhook(innerChannel);
			if (webhook != null)
				return new TranslationChannel(Language.fromString(getLanguageCodeFromChannel(innerChannel.getName())), innerChannel, webhook);

			return null;
		}

		private Webhook getValidWebhook(TextChannel innerChannel) {
			for (Webhook currentWebhook : innerChannel.getWebhooks().complete())
				if (currentWebhook.getName().matches("Translator-[a-z]{2}"))
					return currentWebhook;

			return null;
		}

		private String getLanguageCodeFromChannel(String name) {
			return name.substring(name.length() - 2, name.length());
		}

		private class TranslationPackage {
			private TranslationChannel source;

			private List<TranslationChannel> destinations;

			public TranslationPackage(TranslationChannel source, List<TranslationChannel> destinations) {
				this.source = source;
				this.destinations = destinations;
			}

			public TranslationChannel getSourceChannel() {
				return source;
			}

			public List<TranslationChannel> getDestinationChannels() {
				return destinations;
			}
		}

		private class TranslationChannel {
			private Language language;

			private TextChannel channel;

			private Webhook translationWebhook;

			public TranslationChannel(Language lang, TextChannel chann, Webhook webhook) {
				this.language = lang;
				this.channel = chann;
				this.translationWebhook = webhook;
			}

			public Language getLanguage() {
				return language;
			}

			public TextChannel getChannel() {
				return channel;
			}

			public Webhook getWebhook() {
				return translationWebhook;
			}

			@Override
			public boolean equals(Object object) {
				try {
					TranslationChannel compare = (TranslationChannel) object;

					return this.channel.getId().equals(compare.getChannel().getId());
				} catch (ClassCastException e) {
					return false;
				}
			}
		}
	}

	public static class UpdateBotThread implements Runnable {
		@Override
		public void run() {
			LOGGER.info("The update bot is enabled, began initialization.");

			// Initialize the webhook client to be used for updates.
			WebhookClient client = buildWebhookClient();
			LOGGER.info("Initialized WebhookClient.");

			LOGGER.info("Beginning to request activity from Fanart.TV.");
			while (true) {
				ActivityResponse response = executeActivityRequest();

				// Check if there was an error with the response
				if (response != null && !response.getChanges().isEmpty()) {
					LOGGER.info("Successfully got activity request from Fanart.TV");
					try {
						WebhookMessageBuilder builder = new WebhookMessageBuilder();

						// Add each embed to the builder
						LOGGER.info("Beginning to build message embeds.");
						for (int i = 0; i < response.getChanges().size(); i++) {
							// Send the message and re-initialize the builder if there are more than 10
							// embeds
							if (i % 10 == 0 && i > 0) {
								LOGGER.info("More embeds than possible, sending message and creating new builder.");
								if (!builder.isEmpty())
									client.send(builder.build());
								builder = new WebhookMessageBuilder();
								// Otherwise, add the next embed
							} else {
								LOGGER.info("Generating embed " + (i + 1) + " for Discord message.");
								builder.addEmbeds(generateEmbed(response.getChanges().get(i)));
							}
						}

						LOGGER.info("Sending message to Discord.");
						if (!builder.isEmpty()) {
							// Send the message with embeds
							client.send(builder.build());
						}

						// This will only happy if everything succeeded.
						// Update the time of the last request to now
						updateLastRequestTime(response.getCurrentTimestamp());
					} catch (HttpException e) {
						LOGGER.warn("Failed to send message to discord.", e);
					}
				} else {
					LOGGER.info("No changes detected in response.");
				}

				LOGGER.info("Sleeping for " + SLEEP_TIME_SECONDS + " seconds before requesting activity.");
				// Sleep for two minutes between getting activity from the server
				try {
					Thread.sleep(TimeUnit.SECONDS.toMillis(SLEEP_TIME_SECONDS));
				} catch (InterruptedException e) {
					LOGGER.warn("Application failed to sleep.", e);
				}
			}
		}

		/**
		 * Build the webhook client
		 * 
		 * @return A webhook based on the configuration file
		 */
		private static WebhookClient buildWebhookClient() {
			return new WebhookClientBuilder(Configuration.getWebhookId(), Configuration.getWebhookToken()).build();
		}

		/**
		 * @return HttpRequest for the next applicable time
		 */
		private static HttpUriRequest buildRequest() {
			return RequestBuilder.get().setUri(Configuration.getFanartRequestUrl()).addParameter("api_key", Configuration.getFanartApiKey())
					.addParameter("timestamp", String.valueOf(Configuration.getLastRequestTime())).build();
		}

		/**
		 * Update the last request time with the time from the response
		 */
		private static void updateLastRequestTime(long newTime) {
			Configuration.setLastRequestTime(newTime);
		}

		/**
		 * Default HTTP client for the requests
		 */
		private static HttpClient requestClient = HttpClientBuilder.create().build();
		/**
		 * Default Gson object to use, equipped with a Date deserializer
		 */
		private static Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

		/**
		 * Generate and execute a request for recent activity
		 * 
		 * @return New ActivityResponse
		 */
		private static ActivityResponse executeActivityRequest() {
			try {
				int retryCount = 0;
				String response;
				// Do-while retry count is less than 3 (this will send the first request, and do
				// two subsequent retries if required)
				do {
					// Get the HTTP response body as a string (JSON)
					response = EntityUtils.toString(requestClient.execute(buildRequest()).getEntity());

					try {
						// Return the Gson object
						return gson.fromJson(response, ActivityResponse.class);
					} catch (Exception e) {
						LOGGER.warn("Failed to parse JSON response into GSON Object.", e);
					}
					retryCount++;
				} while (retryCount < 3);
			} catch (IOException e) {
				LOGGER.warn("Failed to get HTTP response from request URL", e);
			}

			// Return null if the request failed
			return null;
		}

		/**
		 * @param responseToParse
		 * @return
		 */
		private static MessageEmbed generateEmbed(ChangeResponse responseToParse) {
			EmbedBuilder builder = new EmbedBuilder();
			// Set the color of the embed
			builder.setColor(getEmbedColor(responseToParse));
			// Set the thumbnail for the embed
			builder.setFooter("", "https://fanart.tv/images/logo_300.png");
			// Set the title of the embed
			builder.setTitle(parseMessageActionText(responseToParse), responseToParse.getImageUrl());

			// Add an author field if the change is only an upload
			if (responseToParse.getType() == ChangeType.UPLOADED)
				builder.addField("Author", String.format("[%s](%s)", responseToParse.getUser(), responseToParse.getUserUrl()), true);
			// Otherwise, add a moderated by field and the author field using the alt user
			// info
			else {
				builder.addField(String.format("%s by", StringUtils.capitalize(ChangeType.getVerb(responseToParse.getType()))),
						String.format("[%s](%s)", responseToParse.getUser(), responseToParse.getUserUrl()), true);
				builder.addField("Author", String.format("[%s](%s)", responseToParse.getAltUser(), responseToParse.getAltUserUrl()), true);
			}

			// Add a link to the object page (Movie/Series/Music)
			String objectTypeField = String.format("[%s](%s)", "Fanart", responseToParse.getModifiedUrl());

			// Add a link to the TMDb if it is a movie
			if (responseToParse.getModifiedSection() == ModifiedSection.MOVIE)
				objectTypeField += String.format(" | [TMDb](%s)", generateTMDbLink(responseToParse.getModifiedId()));
			// Add a link to TheTVDB if it is a show
			else if (responseToParse.getModifiedSection() == ModifiedSection.SERIES)
				objectTypeField += String.format(" | [TheTVDB](%s)", generateTheTVDBLink(responseToParse.getModifiedId()));

			// Add the object link field
			builder.addField(StringUtils.capitalize(ModifiedSection.getVerb(responseToParse.getModifiedSection())), objectTypeField, true);

			// If the image is approved, add a vote link
			if (responseToParse.getType() == ChangeType.APPROVED) {
				builder.addField("Vote", String.format("[Like Image](%s)", generateVoteLink(responseToParse)), true);
				builder.setImage(responseToParse.getImageUrl());
			} else if (responseToParse.getType() == ChangeType.DECLINED) {
				builder.setDescription(responseToParse.getMessage() == null ? "" : responseToParse.getMessage());
			}

			// Set the timestamp of the modification
			builder.setTimestamp(responseToParse.getAdded().toInstant());
			return builder.build();
		}

		/**
		 * @param modifiedId
		 * @return
		 */
		private static Object generateTheTVDBLink(Integer modifiedId) {
			return "https://www.thetvdb.com/?tab=series&id=" + modifiedId;
		}

		/**
		 * @param modifiedId
		 * @return
		 */
		private static String generateTMDbLink(Integer modifiedId) {
			return "https://tmdb.org/movie/" + modifiedId;
		}

		/**
		 * @param responseToParse
		 * @return
		 */
		private static String generateVoteLink(ChangeResponse responseToParse) {
			return String.format("https://fanart.tv/api/setdata.php?type=vote&id=%d&section=%d&ajax=1&current_url=%s", responseToParse.getImageId(),
					responseToParse.getModifiedSection(), responseToParse.getModifiedUrl().substring(responseToParse.getModifiedUrl().lastIndexOf("fanart.tv/") + 9));
		}

		/**
		 * @param responseToParse
		 * @return
		 */
		private static Color getEmbedColor(ChangeResponse responseToParse) {
			switch (responseToParse.getType()) {
			case ChangeType.UPLOADED:
				return new Color(90, 230, 222);
			// return new Color(0, 116, 217);
			case ChangeType.APPROVED:
				return new Color(46, 204, 64);
			case ChangeType.DECLINED:
				return new Color(255, 65, 54);
			default:
				return null;
			}
		}

		/**
		 * @param responseToParse
		 * @return
		 */
		private static String parseMessageActionText(ChangeResponse responseToParse) {
			return String.format("An image was %s for %s", ChangeType.getVerb(responseToParse.getType()), responseToParse.getModifiedName());
		}
	}
}