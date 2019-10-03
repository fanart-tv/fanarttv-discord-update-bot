package tv.fanart.api.model

data class ActivityResponse(
    val timestamp: Long?,
    val currentTimestamp: Long?,
    val changes: List<ChangeResponse>?
)