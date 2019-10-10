package tv.fanart.bot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import tv.fanart.api.FanartApi
import tv.fanart.api.model.ChangeResponse
import tv.fanart.discord.ChangeMapper
import tv.fanart.discord.DiscordWebhookClient
import java.util.*

class UpdateBot(
    private val fanartApi: FanartApi,
    private val changeMapper: ChangeMapper,
    private val webhookClient: DiscordWebhookClient?
) {

    private fun processChanges(changes: List<ChangeResponse>) = try {
        val mapped = changeMapper.mapChanges(changes)
        webhookClient?.sendCards(mapped)?.let { true } ?: false
    } catch (t: Throwable) {
        logger.warn(t) { "Failed to send update webhook messages to Discord" }
        false
    }

    suspend fun update(lastUpdate: Long): Long? {
        logger.debug { "Retrieving changes from Fanart API since ${Date(lastUpdate)}" }
        val activity = try {
            fanartApi.getChanges(after = lastUpdate)
        } catch (t: Throwable) {
            logger.warn(t) { "Failed to retrieve changes from Fanart API" }
            null
        }
        return activity?.let {
            withContext(Dispatchers.IO) {
                logger.debug { "Processing ${activity.changes.size} changes from Fanart API" }
                if (processChanges(activity.changes)) {
                    activity.currentTimestamp.also {
                        logger.info { "Successfully sent messages for ${activity.changes.size} updates" }
                    }
                } else {
                    null
                }
            }
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}