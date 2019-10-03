package tv.fanart.bot

import tv.fanart.config.model.UpdateBotConfiguration
import java.util.*

class UpdateBot(private val updateBotConfiguration: UpdateBotConfiguration) {
    suspend fun update(lastUpdate: Date): Date? {
        return null
    }
}