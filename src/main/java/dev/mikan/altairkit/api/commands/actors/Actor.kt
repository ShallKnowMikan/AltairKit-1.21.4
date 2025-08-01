package dev.mikan.altairkit.api.commands.actors

import dev.mikan.altairkit.AltairKit.Companion.colorize
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

interface Actor {

    val args: List<String>

    val sender : CommandSender

    fun reply(message:String,vararg params:Any) {
        var msg = message
        for (arg in args) {
            msg = message.replaceFirst("\\{}",arg);
        }
        this.sender.sendMessage(colorize(msg).colorize())
    }
    fun isConsole() : Boolean
    fun asConsole() : ConsoleCommandSender?
    fun isPlayer() : Boolean
    fun asPlayer() : Player?

    fun name() : String
    fun hasPermission(perm: String): Boolean
}