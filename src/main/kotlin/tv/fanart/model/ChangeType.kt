package tv.fanart.model

import com.google.gson.annotations.SerializedName

enum class ChangeType {
    @SerializedName("0")
    Uploaded,
    @SerializedName("1")
    Approved,
    @SerializedName("2")
    Declined
}