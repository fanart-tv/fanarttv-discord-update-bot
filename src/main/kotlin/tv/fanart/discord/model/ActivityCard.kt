package tv.fanart.discord.model

import java.awt.Color
import java.time.Instant

data class ActivityCard(
    val title: ActivityCardComponent,
    val moderationMessage: String?,
    val moderationSection: ActivityCardComponent?,
    val authorSection: ActivityCardComponent,
    val typeSection: ActivityCardComponent,
    val voteSection: ActivityCardComponent,
    val imageUrl: String?,
    val embedColor: Color,
    val timestamp: Instant
)