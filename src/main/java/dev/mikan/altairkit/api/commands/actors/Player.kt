package dev.mikan.altairkit.api.commands.actors

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class Player : Actor {

    private var _args: List<String> = emptyList()
    private var _sender: CommandSender? = null

    constructor(sender: CommandSender, args: List<String>) {
        this._sender = sender
        this._args = args
    }

    override var args: List<String>
        get() = _args
        set(value) { _args = value }

    override var sender: CommandSender
        get() = _sender ?: throw IllegalStateException("Sender not initialized")
        set(value) { _sender = value }

    override fun isConsole(): Boolean {
        return false
    }

    override fun asConsole(): ConsoleCommandSender? {
        return null
    }

    override fun isPlayer(): Boolean {
        return true
    }

    override fun asPlayer(): Player {
        return this.sender as Player
    }


    override fun name(): String {
        return this.sender.name
    }

    override fun hasPermission(perm:String): Boolean {
        return this.sender.hasPermission(perm)
    }
}