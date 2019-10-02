package tv.fanart

import tv.fanart.model.UpdateBotConfiguration
import java.util.*

class UpdateBot(private val updateBotConfiguration: UpdateBotConfiguration) {
    suspend fun update(lastUpdate: Date): Date? {
        return null
    }
}