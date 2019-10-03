package tv.fanart

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tv.fanart.api.AuthInterceptor
import tv.fanart.api.FanartApi
import tv.fanart.bot.FanartBot
import tv.fanart.config.ConfigRepo
import tv.fanart.config.model.UpdateConfig
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

        val apiModule = module {
            single {
                AuthInterceptor(get<ConfigRepo>().updateConfig?.apiKey ?: UpdateConfig.DEFAULT_API_KEY)
            }
            single {
                Retrofit.Builder()
                    .baseUrl("https://webservice.fanart.tv")
                    .client(OkHttpClient.Builder().addInterceptor(get<AuthInterceptor>()).build())
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                    .build()
            }
            single { get<Retrofit>().create(FanartApi::class.java) }
        }

        startKoin {
            printLogger()
            modules(listOf(configModule, apiModule))
        }

        FanartBot().start()
    }
}.main(args)