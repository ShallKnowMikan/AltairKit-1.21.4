package dev.mikan.altairkit.utils

import dev.mikan.altairkit.AltairKit.Companion.toMessage
import org.bukkit.entity.Player

object PlayerMethods {

    fun Player.sendMessage(msg: String) {
        this.sendMessage { msg.toMessage() }
    }

}