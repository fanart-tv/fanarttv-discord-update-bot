package tv.fanart

import club.minnced.discord.webhook.WebhookClientBuilder
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
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
import tv.fanart.bot.UpdateBot
import tv.fanart.config.ConfigRepo
import tv.fanart.config.model.UpdateConfig
import tv.fanart.discord.ChangeMapper
import tv.fanart.discord.DiscordWebhookClient
import tv.fanart.util.DateDeserializer
import java.nio.file.Path
import java.nio.file.Paths
import java.time.ZoneId
import java.util.*

fun main(args: Array<String>) = object : CliktCommand() {
    val configPath: Path by option(
        "-c", "--config",
        help = "Location of config file"
    ).path(
        writable = true,
        readable = true
    ).default(
        Paths.get(System.getProperty("user.home"), ".config", "fanart-tv", "discord-bot", "config.hocon")
    )

    val logLevel: String by option(
        "-l", "--logging",
        help = "Loggin level"
    ).choice("OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE").default("INFO")

    override fun run() = runBlocking {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, logLevel)

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
                    .addConverterFactory(
                        GsonConverterFactory.create(
                            GsonBuilder().registerTypeAdapter(
                                Date::class.java,
                                DateDeserializer(
                                    ZoneId.of(
                                        get<ConfigRepo>().updateConfig?.serverTimezone ?: UpdateConfig.DEFAULT_TIMEZONE
                                    )
                                )
                            ).create()
                        )
                    )
                    .build()
            }
            single { get<Retrofit>().create(FanartApi::class.java) }
        }

        val discordModule = module {
            single {
                get<ConfigRepo>().updateConfig?.let {
                    DiscordWebhookClient(
                        WebhookClientBuilder(
                            it.webhookId,
                            it.webhookToken
                        ).build()
                    )
                }
            }
            single {
                ChangeMapper()
            }
        }

        val botModule = module {
            single {
                UpdateBot(get(), get(), get())
            }
        }

        startKoin {
            printLogger()
            modules(listOf(configModule, apiModule, discordModule, botModule))
        }

        FanartBot().start()
    }
}.main(args)