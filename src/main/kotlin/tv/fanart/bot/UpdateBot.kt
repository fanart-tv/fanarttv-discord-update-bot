package tv.fanart.bot

import tv.fanart.api.FanartApi
import tv.fanart.api.model.ChangeResponse
import tv.fanart.discord.ChangeProcessor

class UpdateBot(
    private val fanartApi: FanartApi,
    private val changeProcessor: ChangeProcessor
) {
    
    private fun processChanges(changes: List<ChangeResponse>) = try {
        changeProcessor.processChanges(changes)
        true
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
            if (processChanges(activity.changes)) {
                activity.currentTimestamp
            } else {
                null
            }
        }
    }
}