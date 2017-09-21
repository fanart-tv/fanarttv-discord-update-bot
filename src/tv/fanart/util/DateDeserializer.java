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
package tv.fanart.util;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import tv.fanart.ConfigurationHandler;

public class DateDeserializer implements JsonDeserializer<Date> {
	private static final String[] DATE_FORMATS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", "yyyy-MM-dd"};
	
	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		for (String currentDateFormat : DATE_FORMATS) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(currentDateFormat);
				formatter.setTimeZone(TimeZone.getTimeZone(ConfigurationHandler.getServerTimezone()));
				return formatter.parse(json.getAsString());
			} catch (ParseException e) {}
		}
		throw new JsonParseException("Invalid/unparseable date provided: " + json.getAsString());
	}

}
