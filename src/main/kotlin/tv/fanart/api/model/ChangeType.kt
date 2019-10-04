package tv.fanart.api.model

import com.google.gson.annotations.SerializedName
import java.awt.Color

enum class ChangeType(val embedColor: Color) {
    @SerializedName("0")
    Uploaded(Color(90, 230, 222)),
    @SerializedName("1")
    Approved(Color(46, 204, 64)),
    @SerializedName("2")
    Declined(Color(255, 65, 54))
}