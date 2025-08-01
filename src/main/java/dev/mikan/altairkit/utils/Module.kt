package dev.mikan.altairkit.utils

import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

abstract class Module {
    /*
    * Allows creation and management of
    * modules, useful for big projects
    * */

    protected var listeners: Set<Listener> = emptySet()
    val plugin: Plugin
    val logger: Logger
    val name: String

    constructor(plugin: Plugin,name:String) {
        this.plugin = plugin
        this.logger = plugin.logger
        this.name = name
    }

    abstract fun onEnable()
    abstract fun onReload()
    abstract fun onDisable()
    abstract fun loadConfig()
    abstract fun registerCommands()
    abstract fun registerListeners()

    /*
    * idea:
    *   this.listeners.add(listener)
    *   listen()
    * to register listeners
    * */
    protected fun listen(){
        for (listener in this.listeners) {
            plugin.server.pluginManager.registerEvents(listener,plugin)
        }
    }

    fun info(message:String,vararg params:Any){
        this.logger.info(message)
    }

    fun error(message:String,vararg params:Any){
        this.logger.severe(message)
    }

    fun warning(message:String,vararg params:Any){
        this.logger.warning(message)
    }

    private fun processMessage(message:String, vararg params:Any) : String{
        var msg = message
        for (param in params) {
            msg = message.replaceFirst("\\{}",param.toString())
        }
        return msg
    }
}