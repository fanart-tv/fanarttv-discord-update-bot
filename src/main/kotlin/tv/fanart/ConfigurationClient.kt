package tv.fanart

import java.util.*

class ConfigurationClient {

    // TODO Implement configuration file, in-memory placeholder is temporary

    var lastUpdate: Date = Date()

    val enableUpdateBot: Boolean = true
    val fanartApiKey: String? = "placeholder"
    val updateWebhookId: Long? = 0L
    val updateWebhookToken: String? = "placeholder"

    val enableTranslationBot: Boolean = false
}