package tv.fanart.config.model

import java.time.ZoneId

data class UpdateConfig(
    val lastUpdate: Long = System.currentTimeMillis(),
    val serverTimezone: ZoneId = ZoneId.of("CET"),
    val apiKey: String = "dbb05fa58331b7edbd07aaa1f5c17ae1",

    val webhookId: Long,
    val webhookToken: String,

    val delay: Long = 30
)