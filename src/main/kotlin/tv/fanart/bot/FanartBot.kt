package tv.fanart.bot

import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import tv.fanart.api.FanartApi
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
                while (true) {
                    updateBot.update(lastUpdate)?.let { updateTime ->
                        configurationClient.updateConfig(updateConfig.copy(lastUpdate = updateTime))
                        lastUpdate = updateTime
                    }
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
}