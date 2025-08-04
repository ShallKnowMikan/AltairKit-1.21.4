package dev.mikan.altairkit.test

import dev.mikan.altairkit.api.commands.CmdClass
import dev.mikan.altairkit.api.commands.actors.Actor
import dev.mikan.altairkit.api.commands.annotations.*

class Commands : CmdClass{



    @Command("test mariokart 8 deluxe")
    @Complete("NF","jakehill","joshA", "slim")
    @Permission("dev.mikan.test",false)
    @Sender(User.CONSOLE)
    fun test(actor: Actor,string: Double){
        actor.reply("<green>Received: $string")
    }

    @Command("test mariokart 7")
    @Complete("none")
    @Permission("dev.mikan.test",false)
    @Sender(User.CONSOLE)
    fun test2(actor: Actor,string: Double){
        actor.reply("<green>Received: $string ")
    }

    @Command("test mariobros")
    @Complete("none")
    @Permission("dev.mikan.test",false)
    @Sender(User.CONSOLE)
    fun bros(actor: Actor){
        actor.reply("<green>Playing mario bros. . .</green>")
    }

    @Command("test")
    @Complete("ciao")
    @Permission("dev.mikan.test",false)
    @Sender(User.PLAYER)
    fun offi(actor: Actor,string: String,value: Double){
        actor.reply("<green>Received: $string and $value")
    }

}