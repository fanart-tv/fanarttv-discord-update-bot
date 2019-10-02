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

import com.google.gson.annotations.SerializedName;

/**
 * Enum representing the ISO 639-1 and ISO 639-2
 * abbreviations of many popular languages.
 * 
 * @author Michael Haas
 */
public enum Language {
	@SerializedName("hy")
	Armenian (
		"Armenian",
		"arm",
		"hy"),
	
	@SerializedName("zh")
	Chinese (
		"Chinese",
		"chi",
		"zh"),
	
	@SerializedName("cs")
	Czech (
		"Czech",
		"cze",
		"cs"),
	
	@SerializedName("da")
	Danish (
		"Danish",
		"dan",
		"da"),
	
	@SerializedName("nl")
	Dutch (
		"Dutch",
		"dut",
		"nl"),
	
	@SerializedName("en")
	English (
		"English",
		"eng",
		"en"),
	
	@SerializedName("eo")
	Esperanto (
		"Esperanto",
		"epo",
		"eo"),
	
	@SerializedName("fi")
	Finnish (
		"Finnish",
		"fin",
		"fi"),
	
	@SerializedName("fr")
	French (
		"French",
		"fre",
		"fr"),
	
	@SerializedName("ka")
	Georgian (
		"Georgian",
		"geo",
		"ka"),
	
	@SerializedName("de")
	German (
		"German",
		"ger",
		"de"),
	
	@SerializedName("el")
	Greek (
		"Greek",
		"gre",
		"el"),
	
	@SerializedName("it")
	Italian (
		"Italian",
		"ita",
		"it"),
	
	@SerializedName("ja")
	Japanese (
		"Japanese",
		"jpn",
		"ja"),
	
	@SerializedName("ko")
	Korean (
		"Korean",
		"kor",
		"ko"),
	
	@SerializedName("ku")
	Kurdish (
		"Kurdish",
		"kur",
		"ku"),
	
	@SerializedName("fa")
	Persian (
		"Persian",
		"",
		"fa"),
	
	@SerializedName("pl")
	Polish (
		"Polish",
		"per",
		"pl"),
	
	@SerializedName("pt")
	Portuguese (
		"Portuguese",
		"por",
		"pt"),
	
	@SerializedName("ro")
	Romanian (
		"Romanian",
		"rum",
		"ro"),
	
	@SerializedName("ru")
	Russian (
		"Russian",
		"rus",
		"ru"),
	
	@SerializedName("es")
	Spanish (
		"Spanish",
		"spa",
		"es"),
	
	@SerializedName("sv")
	Swedish (
		"Swedish",
		"swe",
		"sv"),
	
	@SerializedName("tr")
	Turkish (
		"Turkish",
		"tur",
		"tr"),
	
	@SerializedName("ur")
	Urdu (
		"Urdu",
		"urd",
		"ur");

	private final String englishNameOfLanguage, iso639_2, iso639_1;

	private Language(String englishNameOfLanguage, String iso639_2, String iso639_1) {
		this.englishNameOfLanguage = englishNameOfLanguage;
		this.iso639_2 = iso639_2;
		this.iso639_1 = iso639_1;
	}

	public String getLanguage() {
		return englishNameOfLanguage;
	}

	public String getISO639_2() {
		return iso639_2;
	}

	public String etISO639_1() {
		return iso639_1;
	}
}
