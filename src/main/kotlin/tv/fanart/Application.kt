package tv.fanart

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import tv.fanart.bot.FanartBot
import tv.fanart.config.ConfigRepo
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) = object : CliktCommand() {
    val configPath: Path by option(
        "c", "config",
        help = "Location of config file"
    ).path(
        writable = true,
        readable = true
    ).default(
        Paths.get(System.getenv("user.home"), ".config", "fanart-tv", "discord-bot", "config.hocon")
    )

    override fun run() = runBlocking {
        val configModule = module {
            single { ConfigRepo(configPath) }
        }

        startKoin {
            printLogger()
            modules(configModule)
        }

        FanartBot().start()
    }
}.main(args)