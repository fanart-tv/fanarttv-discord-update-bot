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

/**
 * @author Michael Haas
 *
 */
public class ChangeType {
	public static final int UPLOADED = 0;
	public static final int APPROVED = 1;
	public static final int DECLINED = 2;

	public static String getVerb(int changeType) {
		switch (changeType) {
		case UPLOADED:
			return "uploaded";
		case APPROVED:
			return "approved";
		case DECLINED:
			return "declined";
		default:
			return "modified";
		}
	}
}
