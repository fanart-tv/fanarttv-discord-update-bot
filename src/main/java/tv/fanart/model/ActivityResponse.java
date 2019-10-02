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

import java.util.List;

/**
 * @author Michael Haas
 *
 */
public class ActivityResponse {
	/*
	 * Request timestamp
	 */
	private Long timestamp;

	/*
	 * Response timestamp
	 */
	private Long current_timestamp;

	/*
	 * List of changes
	 */
	private List<ChangeResponse> changes;

	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the current_timestamp
	 */
	public Long getCurrentTimestamp() {
		return current_timestamp;
	}

	/**
	 * @return the changes
	 */
	public List<ChangeResponse> getChanges() {
		return changes;
	}
}
