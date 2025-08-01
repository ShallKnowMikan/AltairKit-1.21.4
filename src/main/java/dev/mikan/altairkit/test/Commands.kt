package dev.mikan.altairkit.test

import dev.mikan.altairkit.api.commands.AltairCMD
import dev.mikan.altairkit.api.commands.CmdClass
import dev.mikan.altairkit.api.commands.actors.Actor
import dev.mikan.altairkit.api.commands.annotations.*

class Commands : CmdClass{

    @Command("test mariokart")
    @Complete("ciao")
    @Permission("dev.mikan.test",false)
    @Sender(User.PLAYER)
    fun test(cmd: AltairCMD,actor: Actor,string: Double){
        actor.reply("<green>Received: $string")
    }

}