package tv.fanart

import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import tv.fanart.config.ConfigurationClient
import java.lang.Runnable


class FanartBot : KoinComponent {

    private val configurationClient by inject<ConfigurationClient>()

    private val mainJob = SupervisorJob()
    private val mainContext = Dispatchers.Main + mainJob

    suspend fun start() = coroutineScope {
        configurationClient.updateBotConfiguration?.let { updateBotConfiguration ->
            launch(mainContext) {
                val updateBot = UpdateBot(updateBotConfiguration)
                while (true) {
                    updateBot.update(configurationClient.lastUpdate)?.let {
                        configurationClient.lastUpdate = it
                    }
                    delay(updateBotConfiguration.updateDelay)
                }
            }
        }

        // TODO Spawn off translation bot
        launch(mainContext) {
        }

        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            runBlocking {
                mainJob.cancelAndJoin()
            }
        }))

        yield()
    }
}