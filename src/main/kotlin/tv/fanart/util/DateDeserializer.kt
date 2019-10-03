package tv.fanart.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class DateDeserializer(private val serverTimezone: ZoneId) : JsonDeserializer<Date> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
        for (currentDateFormat in DATE_FORMATS) {
            try {
                val formatter = SimpleDateFormat(currentDateFormat)
                formatter.timeZone = TimeZone.getTimeZone(serverTimezone)
                return formatter.parse(json.asString)
            } catch (e: ParseException) {
            }

        }
        throw JsonParseException("Invalid/unparseable date provided: " + json.asString)
    }

    companion object {
        private val DATE_FORMATS =
            arrayOf("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", "yyyy-MM-dd")
    }

}
