package tv.fanart.bot

import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import tv.fanart.config.ConfigRepo
import java.lang.Runnable
import java.util.*


class FanartBot : KoinComponent {

    private val configurationClient by inject<ConfigRepo>()

    private val mainJob = SupervisorJob()
    private val mainContext = Dispatchers.Main + mainJob

    suspend fun start() = coroutineScope {
        configurationClient.updateConfig?.let { updateConfig ->
            launch(mainContext) {
                val updateBot = UpdateBot(updateConfig)
                while (true) {
                    updateBot.update(Date(updateConfig.lastUpdate))?.let {
                        configurationClient.updateConfig(updateConfig.copy(lastUpdate = it.time))
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