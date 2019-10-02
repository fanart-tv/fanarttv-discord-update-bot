package tv.fanart

import kotlinx.coroutines.*
import java.lang.Runnable


class FanartBot {

    private val mainJob = SupervisorJob()
    private val mainContext = Dispatchers.Main + mainJob

    suspend fun start() = coroutineScope {
        // TODO Get configuration file controller instance

        // TODO Spawn off update bot
        launch(mainContext) {
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