package tv.fanart.bot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.fanart.api.FanartApi
import tv.fanart.api.model.ChangeResponse
import tv.fanart.discord.ChangeMapper
import tv.fanart.discord.DiscordWebhookClient

class UpdateBot(
    private val fanartApi: FanartApi,
    private val changeMapper: ChangeMapper,
    private val webhookClient: DiscordWebhookClient?
) {

    private fun processChanges(changes: List<ChangeResponse>) = try {
        val mapped = changeMapper.mapChanges(changes)
        webhookClient?.sendCards(mapped)?.let { true } ?: false
    } catch (t: Throwable) {
        false
    }

    suspend fun update(lastUpdate: Long): Long? {
        val activity = try {
            fanartApi.getChanges(after = lastUpdate)
        } catch (t: Throwable) {
            null
        }
        return activity?.let {
            withContext(Dispatchers.IO) {
                if (processChanges(activity.changes)) {
                    activity.currentTimestamp
                } else {
                    null
                }
            }
        }
    }
}