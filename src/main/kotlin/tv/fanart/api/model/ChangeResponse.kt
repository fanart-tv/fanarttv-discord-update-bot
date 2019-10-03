package tv.fanart.api.model

import java.util.*

data class ChangeResponse(
    val type: ChangeType?,
    val user: String?,
    val userUrl: String?,
    val altUser: String?,
    val altUserUrl: String?,
    val modifiedSection: ModifiedSection?,
    val modifiedName: String?,
    val modifiedId: Int?,
    val modifiedUrl: String?,
    val imageId: Int?,
    val imageUrl: String?,
    val message: String?,
    val lang: Language?,
    val added: Date?
)
