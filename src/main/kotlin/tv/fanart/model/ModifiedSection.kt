package tv.fanart.model

import com.google.gson.annotations.SerializedName

enum class ModifiedSection {
    @SerializedName("1")
    Series,
    @SerializedName("2")
    Music,
    @SerializedName("3")
    Movie,
    @SerializedName("5")
    Label
}