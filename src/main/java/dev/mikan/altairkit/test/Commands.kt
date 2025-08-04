package dev.mikan.altairkit.test

import dev.mikan.altairkit.api.commands.CmdClass
import dev.mikan.altairkit.api.commands.actors.Actor
import dev.mikan.altairkit.api.commands.annotations.*
import org.bukkit.entity.Player

class Commands : CmdClass{


    @Command("test")
    @Complete("ciao")
    @Permission("dev.mikan.test",false)
    @Sender(User.PLAYER)
    fun offi(actor: Actor, @Default target: Player?, @Default target2: Player?, string: String , @Default target3: Player?){
        actor.reply("<green>Received: $target  $target2 $string $target3")
    }



}