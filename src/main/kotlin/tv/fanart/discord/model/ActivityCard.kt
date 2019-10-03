package tv.fanart.discord.model

import java.awt.Color

data class ActivityCard(
    val titleEmbed: String,
    val moderationMessage: String?,
    val moderationSection: ActivityCardComponent?,
    val authorSection: ActivityCardComponent,
    val typeSection: ActivityCardComponent,
    val voteSection: ActivityCardComponent,
    val imageUrl: String?,
    val embedColor: Color
)