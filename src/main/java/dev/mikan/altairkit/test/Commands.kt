package dev.mikan.altairkit.test

import dev.mikan.altairkit.api.commands.CmdClass
import dev.mikan.altairkit.api.commands.actors.Actor
import dev.mikan.altairkit.api.commands.annotations.*
import org.bukkit.entity.Player
import org.checkerframework.checker.units.qual.min

class Commands : CmdClass{

    @Command("my tette nice")
    @Permission("dev.mikan.test",false)
    @Sender(User.PLAYER)
    fun tee(actor: Actor,
             @Default target: Player?,
             @Complete("my patience","controls me","as the hours pass","before I'm alone")
             string: String,
             @Range(min = 1, max = 2) int: Double){
        actor.reply("<green>Received: $target $string $int")
    }


    @Command("test")
    @Permission("dev.mikan.test",false)
    @Sender(User.PLAYER)
    fun offi(actor: Actor,
             @Default target: Player?,
             @Complete("my patience","controls me","as the hours pass","before I'm alone")
             string: String,
             @Complete("1","2","3")
             @Range(min = 1, max = 2) int: Double){
        actor.reply("<green>Received: $target $string $int")
    }

}