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
public class ModifiedSection {
	public final static int SERIES = 1;
	public final static int MUSIC = 2;
	public final static int MOVIE = 3;
	public final static int LABEL = 5;

	public static String getVerb(int modifiedSection) {
		switch (modifiedSection) {
		case SERIES:
			return "series";
		case MUSIC:
			return "artist";
		case MOVIE:
			return "movie";
		case LABEL:
			return "label";
		default:
			return "object";
		}
	}
}
