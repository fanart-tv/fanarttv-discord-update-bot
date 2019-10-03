package tv.fanart.bot

import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import tv.fanart.api.FanartApi
import tv.fanart.config.ConfigRepo
import java.lang.Runnable


class FanartBot : KoinComponent {

    private val configurationClient by inject<ConfigRepo>()
    private val updateBot by inject<UpdateBot>()

    private val mainJob = SupervisorJob()
    private val mainContext = Dispatchers.Main + mainJob

    suspend fun start() = coroutineScope {
        configurationClient.updateConfig?.let { updateConfig ->
            launch(mainContext) {
                while (true) {
                    updateBot.update(updateConfig.lastUpdate)?.let { updateTime ->
                        configurationClient.updateConfig(updateConfig.copy(lastUpdate = updateTime))
                    }
                    delay(updateConfig.delay)
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