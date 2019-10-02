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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Haas
 *
 */
public class Configuration {
	/*
	 * SLF4J LOGGER
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

	/*
	 * Default configuration file name
	 */
	private static final String CONFIG_FILE_NAME = "config.ini";

	private static final String CONFIG_FILE_LOCATION = SystemUtils.IS_OS_WINDOWS
			? System.getenv("LOCALAPPDATA") + File.separator + "FanartTv" + File.separator + "FanartDiscordBot" + File.separator
			: System.getProperty("user.home") + File.separator + ".config" + File.separator + "FanartDiscordBot" + File.separator;

	/*
	 * Default configuration file location This is either
	 * ~/.config/FanartDiscordBot/config.ini on Non-windows operating systems, and
	 * %LOCALAPPDATA%\FanartDiscordBot\config.ini on Windows.
	 */
	private static final String CONFIG_FILE_PATH = CONFIG_FILE_LOCATION + CONFIG_FILE_NAME;

	private static final File CONFIG_FILE = new File(CONFIG_FILE_PATH);
	private static final File CONFIG_PATH = new File(CONFIG_FILE_LOCATION);

	private static final String DEFAULT_FANARTTV_ACTIVITY_URL = "https://webservice.fanart.tv/v3.2/activity";

	private static final String DEFAULT_FANARTTV_API_KEY = "dbb05fa58331b7edbd07aaa1f5c17ae1";

	/*
	 * Properties object for storing configuration info in memory
	 */
	private static Properties properties;

	/*
	 * Request URL to get updates from Fanart.TV
	 */
	private static String fanartActivityUrl;

	/*
	 * Fanart.TV API Key to use for the activity request
	 */
	private static String fanartApiKey;

	/*
	 * Webhook ID to use for requests
	 */
	private static long activityWebhookId;

	/*
	 * Webhook token to use for requests
	 */
	private static String activityWebhookToken;

	private static String discordAppClientSecret;

	private static long discordGuildId;
	
	private static String microsoftTranslateKey;
	
	private static boolean enableUpdateBot;
	
	private static boolean enableTranslationBot;

	/*
	 * UNIX time stamp of the last time an update was run.
	 */
	private static long lastRequestTime;

	/*
	 * Timezone string of where the Fanart.TV server is located
	 */
	private static String serverTimezone;

	static {
		properties = new Properties();
		// Create a new, default config if the config doesn't exist yet.
		if (!CONFIG_FILE.exists()) {
			LOGGER.warn("No configuration file found, generating now...");
			try {
				CONFIG_PATH.mkdirs();
				CONFIG_FILE.createNewFile();

				// Set default values in the config
				properties.setProperty("FANART_ACTIVITY_URL", DEFAULT_FANARTTV_ACTIVITY_URL);
				properties.setProperty("FANART_API_KEY", DEFAULT_FANARTTV_API_KEY);

				properties.setProperty("WEBHOOK_ID", "");
				properties.setProperty("WEBHOOK_TOKEN", "");
				
				properties.setProperty("MICROSOFT_TRANSLATE_KEY", "");

				properties.setProperty("DISCORD_BOT_TOKEN", "");

				properties.setProperty("DISCORD_GUILD_ID", "");
				
				properties.setProperty("ENABLE_UPDATE_BOT", "FALSE");
				
				properties.setProperty("ENABLE_TRANSLATION_BOT", "TRUE");

				properties.setProperty("LAST_REQUEST_TIME", String.valueOf(System.currentTimeMillis()));
				properties.setProperty("FANART_SERVER_TIMEZONE", "GMT");

				// Store all default values to the config
				properties.store(new FileOutputStream(CONFIG_FILE), null);
			} catch (IOException e) {
				LOGGER.error("Failed to initialize configuration file.", e);
			}

			// Close the application after config creation, to allow the user to fill out
			// the info
			LOGGER.warn("Please fill out the configuration file, then restart the application.\n" + "The config file is located at: " + CONFIG_FILE.getAbsolutePath() + ".\n"
					+ "Given a standard webhook URL, the format is as follows:\n" + "https://discordapp.com/api/webhooks/{WEBHOOK ID}/{WEBHOOK TOKEN}\n"
					+ "Please fill these out in the config.");
			System.exit(0);
		} else {
			// Read existing config info if it exists
			try {
				properties.load(new FileInputStream(CONFIG_FILE));
			} catch (IOException e) {
				LOGGER.error("Failed to load existing config file.", e);
			}

			// Read existing config values
			fanartActivityUrl = properties.getProperty("FANART_ACTIVITY_URL");
			fanartApiKey = properties.getProperty("FANART_API_KEY");

			activityWebhookId = Long.valueOf(properties.getProperty("WEBHOOK_ID"));
			activityWebhookToken = properties.getProperty("WEBHOOK_TOKEN");
			
			microsoftTranslateKey = properties.getProperty("MICROSOFT_TRANSLATE_KEY");
			
			discordAppClientSecret = properties.getProperty("DISCORD_BOT_TOKEN");

			discordGuildId = Long.valueOf(properties.getProperty("DISCORD_GUILD_ID"));
			
			enableUpdateBot = Boolean.valueOf(properties.getProperty("ENABLE_UPDATE_BOT"));
			
			enableTranslationBot = Boolean.valueOf(properties.getProperty("ENABLE_TRANSLATION_BOT"));

			lastRequestTime = Long.valueOf(properties.getProperty("LAST_REQUEST_TIME"));
			serverTimezone = properties.getProperty("FANART_SERVER_TIMEZONE");
		}
	}

	private static void setConfigValue(String property, String value) {
		properties.setProperty(property, value);

		try {
			properties.store(new FileOutputStream(CONFIG_FILE), null);
		} catch (IOException e) {
			LOGGER.error("Failed to update configuration parameter: " + property + " with value " + value, e);
		}
	}

	/**
	 * @return The configured request URL for Fanart.TV
	 */
	public static String getFanartRequestUrl() {
		return fanartActivityUrl;
	}

	/**
	 * @return The configured Fanart.TV API Key
	 */
	public static String getFanartApiKey() {
		return fanartApiKey;
	}

	public static long getWebhookId() {
		return activityWebhookId;
	}

	public static String getWebhookToken() {
		return activityWebhookToken;
	}
	
	public static String getMicrosoftTranslateKey() {
		return microsoftTranslateKey;
	}

	/**
	 * @return The last time a request was made.
	 */
	public static long getLastRequestTime() {
		return lastRequestTime;
	}

	public static long getDiscordGuildId() {
		return discordGuildId;
	}

	public static String getDiscordAppClientSecret() {
		return discordAppClientSecret;
	}

	/**
	 * Update the last request time in the config with the new value.
	 * 
	 * @param newTime
	 *            The new update time to use
	 */
	public static void setLastRequestTime(long newTime) {
		lastRequestTime = newTime;
		setConfigValue("LAST_REQUEST_TIME", String.valueOf(newTime));
	}

	/**
	 * @return the serverTimezone
	 */
	public static String getServerTimezone() {
		return serverTimezone;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * @return the configFileName
	 */
	public static String getConfigFileName() {
		return CONFIG_FILE_NAME;
	}

	/**
	 * @return the configFileLocation
	 */
	public static String getConfigFileLocation() {
		return CONFIG_FILE_LOCATION;
	}

	/**
	 * @return the configFilePath
	 */
	public static String getConfigFilePath() {
		return CONFIG_FILE_PATH;
	}

	/**
	 * @return the configFile
	 */
	public static File getConfigFile() {
		return CONFIG_FILE;
	}

	/**
	 * @return the configPath
	 */
	public static File getConfigPath() {
		return CONFIG_PATH;
	}

	/**
	 * @return the defaultFanarttvActivityUrl
	 */
	public static String getDefaultFanarttvActivityUrl() {
		return DEFAULT_FANARTTV_ACTIVITY_URL;
	}

	/**
	 * @return the defaultFanarttvApiKey
	 */
	public static String getDefaultFanarttvApiKey() {
		return DEFAULT_FANARTTV_API_KEY;
	}

	/**
	 * @return the properties
	 */
	public static Properties getProperties() {
		return properties;
	}

	/**
	 * @return the fanartActivityUrl
	 */
	public static String getFanartActivityUrl() {
		return fanartActivityUrl;
	}

	/**
	 * @return the activityWebhookId
	 */
	public static long getActivityWebhookId() {
		return activityWebhookId;
	}

	/**
	 * @return the activityWebhookToken
	 */
	public static String getActivityWebhookToken() {
		return activityWebhookToken;
	}

	/**
	 * @return the enableUpdateBot
	 */
	public static boolean getEnableUpdateBot() {
		return enableUpdateBot;
	}

	/**
	 * @return the enableTranslationBot
	 */
	public static boolean getEnableTranslationBot() {
		return enableTranslationBot;
	}
}
