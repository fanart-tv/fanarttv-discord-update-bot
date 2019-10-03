package tv.fanart.api.model

import com.google.gson.annotations.SerializedName

data class ActivityResponse(
    val timestamp: Long,
    @SerializedName("current_timestamp")
    val currentTimestamp: Long,
    val changes: List<ChangeResponse>
)