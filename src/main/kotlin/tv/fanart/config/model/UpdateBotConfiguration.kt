package tv.fanart.config.model

import java.time.ZoneId

data class UpdateBotConfiguration(
    val fanartServerTimezone: ZoneId,
    val fanartApiKey: String,

    val webhookId: Long,
    val webhookToken: String,

    val updateDelay: Long
)