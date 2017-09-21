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
package tv.fanart.model;

import java.util.Date;

import tv.fanart.util.Language;

/**
 * @author Michael Haas
 *
 */
public class ChangeResponse {
	/*
	 * Type of change (use ChangeType)
	 */
	private Integer type;
	
	/*
	 * Username of primary user
	 */
	private String user;
	
	/*
	 * Profile url of primary user
	 */
	private String user_url;
	
	/*
	 * Username of secondary user
	 */
	private String alt_user;
	
	/*
	 * Profile url of secondary user
	 */
	private String alt_user_url;
	
	/*
	 * Section modified in change (use ModifiedSection)
	 */
	private Integer modified_section;
	
	/*
	 * Name of modified object
	 */
	private String modified_name;
	
	/*
	 * ID of modified object
	 */
	private Integer modified_id;
	
	/*
	 * URL of modified object
	 */
	private String modified_url;
	
	/*
	 * Image ID of uploaded iamge
	 */
	private Integer image_id;
	
	/*
	 * URL of modified image
	 */
	private String image_url;
	
	/*
	 * Denial message if applicable
	 */
	private String message;
	
	/*
	 * Language of modification
	 */
	private Language lang;
	
	/*
	 * Date the modification was made
	 */
	private Date added;

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the user_url
	 */
	public String getUserUrl() {
		return user_url;
	}

	/**
	 * @return the alt_user
	 */
	public String getAltUser() {
		return alt_user;
	}

	/**
	 * @return the alt_user_url
	 */
	public String getAltUserUrl() {
		return alt_user_url;
	}

	/**
	 * @return the modified_section
	 */
	public Integer getModifiedSection() {
		return modified_section;
	}

	/**
	 * @return the modified_name
	 */
	public String getModifiedName() {
		return modified_name;
	}

	/**
	 * @return the modified_id
	 */
	public Integer getModifiedId() {
		return modified_id;
	}

	/**
	 * @return the modified_url
	 */
	public String getModifiedUrl() {
		return modified_url;
	}

	/**
	 * @return the image_id
	 */
	public Integer getImageId() {
		return image_id;
	}

	/**
	 * @return the image_url
	 */
	public String getImageUrl() {
		return image_url;
	}

	/**
	 * @return the lang
	 */
	public Language getLang() {
		return lang;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the added
	 */
	public Date getAdded() {
		return added;
	}
}
