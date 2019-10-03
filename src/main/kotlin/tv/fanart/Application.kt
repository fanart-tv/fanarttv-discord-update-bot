package tv.fanart

import org.koin.core.context.startKoin
import org.koin.dsl.module
import tv.fanart.config.ConfigurationClient

suspend fun main() {
    val configModule = module {
        single { ConfigurationClient() }
    }

    startKoin {
        printLogger()
        modules(configModule)
    }

    FanartBot().start()
}