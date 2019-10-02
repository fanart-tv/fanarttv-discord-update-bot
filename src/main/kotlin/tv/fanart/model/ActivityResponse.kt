package tv.fanart.model

data class ActivityResponse(
    val timestamp: Long?,
    val currentTimestamp: Long?,
    val changes: List<ChangeResponse>?
)