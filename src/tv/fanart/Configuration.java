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

	/*
	 * Default configuration file location This is either
	 * ~/.config/FanartDiscordBot/config.ini on Non-windows operating systems, and
	 * %LOCALAPPDATA%\FanartDiscordBot\config.ini on Windows.
	 */
	private static final String CONFIG_FILE_LOCATION = SystemUtils.IS_OS_WINDOWS
			? System.getenv("LOCALAPPDATA") + File.separator + "FanartDiscordBot" + File.separator + CONFIG_FILE_NAME
			: System.getProperty("user.home") + File.separator + ".config" + File.separator + "FanartDiscordBot" + File.separator + CONFIG_FILE_NAME;

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
	private static long webhookId;

	/*
	 * Webhook token to use for requests
	 */
	private static String webhookToken;

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
		File configFile = new File(CONFIG_FILE_LOCATION);
		// Create a new, default config if the config doesn't exist yet.
		if (!configFile.exists()) {
			LOGGER.warn("No configuration file found, generating now...");
			try {
				configFile.mkdirs();
				configFile.createNewFile();

				// Set default values in the config
				properties.setProperty("FANART_ACTIVITY_URL", DEFAULT_FANARTTV_ACTIVITY_URL);
				properties.setProperty("FANART_API_KEY", DEFAULT_FANARTTV_API_KEY);
				properties.setProperty("WEBHOOK_ID", "");
				properties.setProperty("WEBHOOK_TOKEN", "");
				properties.setProperty("LAST_REQUEST_TIME", String.valueOf(System.currentTimeMillis()));
				properties.setProperty("FANART_SERVER_TIMEZONE", "CET");

				// Store all default values to the config
				properties.store(new FileOutputStream(configFile), null);
			} catch (IOException e) {
				LOGGER.error("Failed to initialize configuration file.", e);
			}

			// Close the application after config creation, to allow the user to fill out
			// the info
			LOGGER.warn("Please fill out the configuration file, then restart the application.\n" + "The config file is located at: " + configFile.getAbsolutePath() + ".\n"
					+ "Given a standard webhook URL, the format is as follows:\n" + "https://discordapp.com/api/webhooks/{WEBHOOK ID}/{WEBHOOK TOKEN}\n"
					+ "Please fill these out in the config.");
			System.exit(0);
		} else {
			// Read existing config info if it exists
			try {
				properties.load(new FileInputStream(configFile));
			} catch (IOException e) {
				LOGGER.error("Failed to load existing config file.", e);
			}

			// Read existing config values
			fanartActivityUrl = properties.getProperty("FANART_ACTIVITY_URL");
			fanartApiKey = properties.getProperty("FANART_API_KEY");
			webhookId = Long.valueOf(properties.getProperty("WEBHOOK_ID"));
			webhookToken = properties.getProperty("WEBHOOK_TOKEN");
			lastRequestTime = Long.valueOf(properties.getProperty("LAST_REQUEST_TIME"));
			serverTimezone = properties.getProperty("FANART_SERVER_TIMEZONE");
		}
	}

	private static void setConfigValue(String property, String value) {
		properties.setProperty(property, value);

		try {
			properties.store(new FileOutputStream(new File(CONFIG_FILE_LOCATION)), null);
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

	/**
	 * 
	 * @return The configured Webhook ID
	 */
	public static long getWebhookId() {
		return webhookId;
	}

	/**
	 * @return The configured Webhook Token
	 */
	public static String getWebhookToken() {
		return webhookToken;
	}

	/**
	 * @return The last time a request was made.
	 */
	public static long getLastRequestTime() {
		return lastRequestTime;
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
}
