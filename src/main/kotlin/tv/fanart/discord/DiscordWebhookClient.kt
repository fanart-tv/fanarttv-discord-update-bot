package tv.fanart.discord

import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import net.dv8tion.jda.api.EmbedBuilder
import tv.fanart.discord.model.ActivityCard
import tv.fanart.discord.model.ActivityCardComponent

class DiscordWebhookClient(
    private val webhookClient: WebhookClient
) {

    private fun WebhookEmbedBuilder.addSection(section: ActivityCardComponent?) {
        section?.let {
            addField(WebhookEmbed.EmbedField(true, it.string, it.embed))
        }
    }

    private fun buildEmbeds(cards: List<ActivityCard>) = cards.map { card ->
        val builder = WebhookEmbedBuilder()
        builder.setTitle(WebhookEmbed.EmbedTitle(card.title.string, card.title.embed))

        builder.setDescription(card.moderationMessage)
        builder.addSection(card.moderationSection)

        builder.addSection(card.authorSection)
        builder.addSection(card.typeSection)
        builder.addSection(card.voteSection)

        builder.setImageUrl(card.imageUrl)

        builder.setColor(card.embedColor.rgb)
        builder.setTimestamp(card.timestamp)

        builder.build()
    }

    fun sendCards(cards: List<ActivityCard>) = cards.chunked(10).forEach { requestCards ->
        val embeds = buildEmbeds(requestCards)
        webhookClient.send(embeds).join()
        throw Exception()
    }
}