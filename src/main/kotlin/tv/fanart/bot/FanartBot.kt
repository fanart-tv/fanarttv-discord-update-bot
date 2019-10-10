package tv.fanart.bot

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import tv.fanart.config.ConfigRepo
import tv.fanart.config.model.UpdateConfig
import java.lang.Runnable
import java.util.concurrent.TimeUnit

class FanartBot : KoinComponent {

    private val configurationClient by inject<ConfigRepo>()
    private val updateBot by inject<UpdateBot>()

    private val mainJob = SupervisorJob()
    private val mainContext = Dispatchers.Unconfined + mainJob

    suspend fun start() = coroutineScope {
        configurationClient.updateConfig?.let { updateConfig ->
            launch(mainContext) {
                var lastUpdate = updateConfig.lastUpdate ?: System.currentTimeMillis()
                logger.info { "Beginning update polling on an interval of ${updateConfig.delay} seconds" }
                while (true) {
                    logger.debug { "Requesting updates to post to Discord" }
                    updateBot.update(lastUpdate)?.let { updateTime ->
                        logger.debug { "Saving new update time to config" }
                        configurationClient.updateConfig(updateConfig.copy(lastUpdate = updateTime))
                        lastUpdate = updateTime
                    } ?: logger.debug { "Update failed, not saving last update time" }
                    logger.debug { "Sleeping for ${updateConfig.delay}" }
                    delay(TimeUnit.SECONDS.toMillis(updateConfig.delay ?: UpdateConfig.DEFAULT_DELAY))
                }
            }
        }

        configurationClient.translationConfig?.let {
            launch(mainContext) {
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            runBlocking {
                mainJob.cancelAndJoin()
            }
        }))

        yield()
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}