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
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.exceptions.HttpException;
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

	private static final int SLEEP_TIME_SECONDS = 30;

	/**
	 * @param args
	 *            The bot does not take arguments, and instead uses a configuration
	 *            file that generates on the first run.
	 */
	public static void main(String[] args) {
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
							client.send(builder.build());
							builder = new WebhookMessageBuilder();
							// Otherwise, add the next embed
						} else {
							LOGGER.info("Generating embed " + (i + 1) + " for Discord message.");
							builder.addEmbeds(generateEmbed(response.getChanges().get(i)));
						}
					}

					LOGGER.info("Sending message to Discord.");
					// Send the message with embeds
					client.send(builder.build());

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
		return new WebhookClientBuilder(ConfigurationHandler.getWebhookId(), ConfigurationHandler.getWebhookToken()).build();
	}

	/**
	 * Default request generated at runtime
	 */
	private static final RequestBuilder DEFAULT_REQUEST_BUILDER = RequestBuilder.get().setUri(ConfigurationHandler.getFanartRequestUrl()).addParameter("api_key",
			ConfigurationHandler.getFanartApiKey());

	/**
	 * @return HttpRequest for the next applicable time
	 */
	private static HttpUriRequest buildRequest() {
		return DEFAULT_REQUEST_BUILDER.addParameter("timestamp", String.valueOf(ConfigurationHandler.getLastRequestTime())).build();
	}

	/**
	 * Update the last request time with the time from the response
	 */
	private static void updateLastRequestTime(long newTime) {
		ConfigurationHandler.setLastRequestTime(newTime);
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
			String response = EntityUtils.toString(requestClient.execute(buildRequest()).getEntity());
			return gson.fromJson(response, ActivityResponse.class);
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