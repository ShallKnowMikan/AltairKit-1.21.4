package dev.mikan.altairkit.utils

import dev.mikan.altairkit.AltairKit.Companion.toMessage
import org.bukkit.Bukkit

object Logger {

    fun info(message: String) {
        Bukkit.getConsoleSender().sendMessage { message.toMessage() }
    }

    fun warning(message: String) {
        Bukkit.getConsoleSender().sendMessage { "<yellow>$message".toMessage() }
    }

    fun error(message: String) {
        Bukkit.getConsoleSender().sendMessage { "<red>$message".toMessage() }
    }

}