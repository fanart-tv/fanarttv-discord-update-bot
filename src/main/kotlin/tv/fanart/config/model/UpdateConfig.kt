package tv.fanart.config.model

import java.time.ZoneId

data class UpdateConfig(
    val lastUpdate: Long = System.currentTimeMillis(),
    val serverTimezone: String = "CET",
    val apiKey: String = DEFAULT_API_KEY,

    val webhookId: Long,
    val webhookToken: String,

    val delay: Long = 30
) {
    companion object {
        const val DEFAULT_API_KEY = "dbb05fa58331b7edbd07aaa1f5c17ae1"
    }
}