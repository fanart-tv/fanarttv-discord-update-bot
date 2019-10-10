package tv.fanart.config

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import io.github.config4k.extract
import io.github.config4k.toConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import tv.fanart.config.model.ConfigFile
import tv.fanart.config.model.UpdateConfig
import java.nio.file.Files
import java.nio.file.Path

class ConfigRepo(private val configPath: Path) {

    private val configFile by lazy {
        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath.parent)
            Files.createFile(configPath)
        }
        ConfigFactory.parseFile(configPath.toFile()).extract<ConfigFile>().also {
            if (it.updates == null) {
                logger.info { "Update bot not configured" }
            }
            logger.info { "Translation bot not configured" }
        }
    }

    val updateConfig by lazy { configFile.updates }

    val translationConfig = null

    suspend fun updateConfig(configFile: ConfigFile) {
        withContext(Dispatchers.IO) {
            Files.writeString(
                configPath,
                configFile.toConfig("fanart").getConfig("fanart").root().render(
                    ConfigRenderOptions.defaults().setJson(false).setOriginComments(false)
                )
            )
        }
    }

    suspend fun updateConfig(updateConfig: UpdateConfig) = updateConfig(configFile.copy(updates = updateConfig))

    companion object {
        val logger = KotlinLogging.logger { }
    }
}