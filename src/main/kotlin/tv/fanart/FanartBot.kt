package tv.fanart

import kotlinx.coroutines.*
import java.lang.Runnable


class FanartBot(private val configurationClient: ConfigurationClient) {

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