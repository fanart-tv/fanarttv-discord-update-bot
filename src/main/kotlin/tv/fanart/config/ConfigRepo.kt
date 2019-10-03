package tv.fanart.config

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.github.config4k.toConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tv.fanart.config.model.ConfigFile
import tv.fanart.config.model.UpdateConfig
import java.nio.file.Files
import java.nio.file.Path

class ConfigRepo(private val configPath: Path) {

    private val configFile by lazy {
        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath.parent)
        }
        ConfigFactory.parseFile(configPath.toFile()).extract<ConfigFile>()
    }

    val updateConfig by lazy { configFile.updates }

    val translationConfig = null

    suspend fun updateConfig(configFile: ConfigFile) {
        withContext(Dispatchers.IO) {
            Files.writeString(configPath, configFile.toConfig("").atKey("").root().render())
        }
    }

    suspend fun updateConfig(updateConfig: UpdateConfig) = updateConfig(configFile.copy(updates = updateConfig))
}