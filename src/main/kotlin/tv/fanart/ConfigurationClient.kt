package tv.fanart

import tv.fanart.model.UpdateBotConfiguration
import java.time.ZoneId
import java.util.*

class ConfigurationClient {

    // TODO Implement configuration file, in-memory placeholder is temporary

    var lastUpdate: Date = Date()

    private val enableUpdateBot: Boolean = true
    private val fanartServerTimezone: ZoneId? = null
    private val fanartApiKey: String? = "placeholder"
    private val updateWebhookId: Long? = 0L
    private val updateWebhookToken: String? = "placeholder"
    private val updateDelaySeconds: Long? = null

    val enableTranslationBot: Boolean = false

    val updateBotConfiguration
        get() = if (enableUpdateBot && fanartApiKey != null && updateWebhookId != null && updateWebhookToken != null) {
            UpdateBotConfiguration(
                fanartServerTimezone ?: ZoneId.of("CET"),
                fanartApiKey,
                updateWebhookId,
                updateWebhookToken,
                updateDelaySeconds ?: 30L
            )
        } else {
            null
        }
}