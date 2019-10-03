package tv.fanart.discord

import tv.fanart.api.model.ChangeResponse
import tv.fanart.api.model.ChangeType
import tv.fanart.api.model.ModifiedSection
import tv.fanart.discord.model.ActivityCard
import tv.fanart.discord.model.ActivityCardComponent

class ChangeProcessor {

    private fun String.linkable(url: String) = "[$this]($url)"

    private fun createTitleEmbed(changeType: ChangeType, modifiedName: String, imageUrl: String) =
        "An image was ${changeType.name.toLowerCase()} for $modifiedName".linkable(imageUrl)

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

    fun processChanges(changes: List<ChangeResponse>) {
        changes.map { change ->
            ActivityCard(
                titleEmbed = createTitleEmbed(change.type, change.modifiedName, change.imageUrl),
                moderationMessage = change.message,
                moderationSection = change.altUser?.let {
                    ActivityCardComponent("${change.type.name} by", change.user.linkable(change.userUrl))
                },
                authorSection = ActivityCardComponent(
                    "Author",
                    (change.altUser ?: change.user).linkable(change.altUserUrl ?: change.userUrl)
                ),
                typeSection = ActivityCardComponent(
                    change.modifiedSection.name,
                    createSectionEmbed(change.modifiedSection, change.modifiedId, change.modifiedUrl)
                ),
                voteSection = ActivityCardComponent(
                    "Vote",
                    createVoteEmbed(change.modifiedId, change.modifiedSection, change.modifiedUrl)
                ),
                imageUrl = change.imageUrl.takeIf { change.type == ChangeType.Approved },
                embedColor = change.type.embedColor
            )
        }
    }
}