package tv.fanart.bot

import tv.fanart.config.model.UpdateConfig
import java.util.*

class UpdateBot(private val updateConfig: UpdateConfig) {
    suspend fun update(lastUpdate: Date): Date? {
        println("test")
        return null
    }
}