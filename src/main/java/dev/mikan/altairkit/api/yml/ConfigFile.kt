package dev.mikan.altairkit.api.yml

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.concurrent.CompletableFuture

data class ConfigFile(
    val plugin: Plugin,
    val fileName: String,

) : YamlConfiguration() {
    val file: File = File(plugin.dataFolder,fileName)

    init {
        this.load(file)
    }

    fun save() {
        try {
            CompletableFuture.runAsync {
                this.save(file)
            }
        } catch (e: Exception) {e.printStackTrace()}
    }
}