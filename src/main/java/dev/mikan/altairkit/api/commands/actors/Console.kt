package dev.mikan.altairkit.api.commands.actors

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class Console : Actor {

    private var _args = emptyList<String>()
    private var _sender : CommandSender? = null

    constructor(sender: CommandSender,args: List<String>) {
        this._sender = sender
        this._args = args
    }

    override var args: List<String>
        get() = _args
        set(value) {
            _args = value
        }

    override var sender: CommandSender
        get() = _sender?: throw IllegalStateException("Sender not initialized")
        set(value) {_sender = value}

    override fun isConsole(): Boolean {
        return true
    }

    override fun asConsole(): ConsoleCommandSender {
        return this.sender as ConsoleCommandSender
    }

    override fun isPlayer(): Boolean {
        return false
    }

    override fun asPlayer(): Player? {
        return null
    }


    override fun name(): String {
        return this.sender.name
    }

    override fun hasPermission(perm:String): Boolean {
        return this.sender.hasPermission(perm)
    }
}