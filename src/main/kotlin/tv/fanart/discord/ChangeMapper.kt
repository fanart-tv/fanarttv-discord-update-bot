package tv.fanart.discord

import tv.fanart.api.model.ChangeResponse
import tv.fanart.api.model.ChangeType
import tv.fanart.api.model.ModifiedSection
import tv.fanart.discord.model.ActivityCard
import tv.fanart.discord.model.ActivityCardComponent
import java.time.Instant

class ChangeMapper {

    private fun String.linkable(url: String) = "[$this]($url)"

    private fun createTitleEmbed(changeType: ChangeType, modifiedName: String, imageUrl: String) =
        ActivityCardComponent("An image was ${changeType.name.toLowerCase()} for $modifiedName", imageUrl)

    private fun createSectionEmbed(modifiedSection: ModifiedSection, modifiedId: Long, modifiedUrl: String): String {
        var embed = "Fanart".linkable(modifiedUrl)
        if (modifiedSection == ModifiedSection.Series || modifiedSection == ModifiedSection.Movie) {
            embed += " | "
            embed += when (modifiedSection) {
                ModifiedSection.Movie -> "TMDb".linkable("https://tmdb.org/movie/$modifiedId")
                ModifiedSection.Series -> "TheTVDB".linkable("https://www.thetvdb.com/?tab=series&id=$modifiedId")
                else -> null
            }
        }
        return embed
    }

    private fun createVoteEmbed(modifiedId: Long, modifiedSection: ModifiedSection, modifiedUrl: String) =
        "Like Image".linkable(
            "https://fanart.tv/api/setdata.php?type=vote&id=$modifiedId&section=${modifiedSection.id}&ajax=1&current_url=${modifiedUrl.substringAfter(
                "fanart.tv"
            )}"
        )

    fun mapChanges(changes: List<ChangeResponse>) = changes.map { change ->
        ActivityCard(
            title = createTitleEmbed(change.type, change.modifiedName, change.imageUrl),
            moderationMessage = change.message,
            moderationSection = if (change.type != ChangeType.Uploaded) {
                change.altUser?.takeIf { it.isNotBlank() }.let {
                    ActivityCardComponent("${change.type.name} by", change.user.linkable(change.userUrl))
                }
            } else null,
            authorSection = ActivityCardComponent(
                "Author",
                (change.altUser?.takeIf { it.isNotBlank() } ?: change.user).linkable(
                    change.altUserUrl?.takeIf { it.isNotBlank() } ?: change.userUrl
                )
            ),
            typeSection = ActivityCardComponent(
                change.modifiedSection.name,
                createSectionEmbed(change.modifiedSection, change.modifiedId, change.modifiedUrl)
            ),
            voteSection = if (change.type == ChangeType.Approved) {
                ActivityCardComponent(
                    "Vote",
                    createVoteEmbed(change.modifiedId, change.modifiedSection, change.modifiedUrl)
                )
            } else null,
            imageUrl = change.imageUrl.takeIf { change.type == ChangeType.Approved },
            embedColor = change.type.embedColor,
            timestamp = Instant.ofEpochMilli(change.added.time)
        )
    }
}