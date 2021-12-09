package tv.fanart.config.model

data class UpdateConfig(
    val lastUpdate: Long?,
    val serverTimezone: String?,
    val apiKey: String?,
    val apiUrl: String?,

    val webhookId: Long,
    val webhookToken: String,

    val delay: Long?
) {
    companion object {
        const val DEFAULT_TIMEZONE = "GMT"
        const val DEFAULT_API_KEY = "dbb05fa58331b7edbd07aaa1f5c17ae1"
        const val DEFAULT_DELAY = 30L
    }
}