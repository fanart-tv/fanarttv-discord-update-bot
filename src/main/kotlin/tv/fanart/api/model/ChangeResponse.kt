package tv.fanart.api.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ChangeResponse(
    val type: ChangeType,
    val user: String,
    @SerializedName("user_url")
    val userUrl: String,
    @SerializedName("alt_user")
    val altUser: String?,
    @SerializedName("alt_user_url")
    val altUserUrl: String?,
    @SerializedName("modified_section")
    val modifiedSection: ModifiedSection,
    @SerializedName("modified_name")
    val modifiedName: String,
    @SerializedName("modified_id")
    val modifiedId: Long,
    @SerializedName("modified_url")
    val modifiedUrl: String,
    @SerializedName("image_id")
    val imageId: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val message: String?,
    val lang: Language?,
    val added: Date
)
