package tv.fanart.api.model

import com.google.gson.annotations.SerializedName

enum class ModifiedSection(val id: Int) {
    @SerializedName("1")
    Series(1),
    @SerializedName("2")
    Music(2),
    @SerializedName("3")
    Movie(3),
    @SerializedName("5")
    Label(5)
}