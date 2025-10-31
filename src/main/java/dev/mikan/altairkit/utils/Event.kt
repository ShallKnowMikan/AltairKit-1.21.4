package dev.mikan.altairkit.utils

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class Event : Event(), Cancellable{

    /*
    * Just a faster way to create events
    * a good practice is to call the event,
    * then its run method and in it add as first line
    *
    * if(cancelled) return;
    *
    * So that once it is cancelled it won't run
    * */

    private var cancelled: Boolean = false

    companion object {
        private val HANDLERS: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() : HandlerList {
            return HANDLERS
        }
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }



    override fun isCancelled() : Boolean{
        return cancelled
    }

    override fun setCancelled(b: Boolean){
        cancelled = b
    }

    abstract fun run()
}