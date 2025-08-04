package dev.mikan.altairkit.test

import dev.mikan.altairkit.api.commands.AltairCMD
import dev.mikan.altairkit.api.commands.CmdClass
import dev.mikan.altairkit.api.commands.actors.Actor
import dev.mikan.altairkit.api.commands.annotations.*

class Commands : CmdClass{



    @Command("test mariokart 8 deluxe")
    @Complete("ciao","jakehill","joshA", "slim")
    @Permission("dev.mikan.test",false)
    @Sender(User.CONSOLE)
    fun test(cmd: AltairCMD,actor: Actor,string: Double){
        actor.reply("<green>Complete: ${cmd.complete?.args?.get(0)}")
        actor.reply("<green>Permission: ${cmd.permission?.permission}")
        actor.reply("<green>Sender type: ${cmd.sender?.user}")
    }

    @Command("test mariokart 7")
    @Complete("none")
    @Permission("dev.mikan.test",false)
    @Sender(User.CONSOLE)
    fun test2(cmd: AltairCMD,actor: Actor,string: Double){
        actor.reply("<green>Complete: ${cmd.complete?.args?.get(0)}")
        actor.reply("<green>Permission: ${cmd.permission?.permission}")
        actor.reply("<green>Sender type: ${cmd.sender?.user}")
    }

    @Command("test mariobros")
    @Complete("none")
    @Permission("dev.mikan.test",false)
    @Sender(User.CONSOLE)
    fun bros(cmd: AltairCMD,actor: Actor){
        actor.reply("<green>Playing mario bros. . .</green>")
    }

    @Command("test")
    @Complete("ciao")
    @Permission("dev.mikan.test",false)
    @Sender(User.CONSOLE)
    fun offi(cmd: AltairCMD,actor: Actor,string: String,value: Double){
        actor.reply("<green>Received: $string")
    }

}