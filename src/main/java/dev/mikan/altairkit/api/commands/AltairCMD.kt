package dev.mikan.altairkit.api.commands

import dev.mikan.altairkit.AltairKit.Companion.isParsableToDouble
import dev.mikan.altairkit.AltairKit.Companion.isParsableToInt
import dev.mikan.altairkit.api.commands.annotations.*
import dev.mikan.altairkit.utils.Cmd
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class AltairCMD(
    name: String,
    private val onPerform: KFunction<*>,
    private val instance: Any,
    val command: Command? = null,
    val complete: Complete? = null,
    val sender: Sender? = null,
    val permission: Permission? = null,
) : BukkitCommand(name) {

    val subcommands = mutableSetOf<String>()

    override fun execute(
        sender: CommandSender,
        cmdName: String,
        args: Array<out String>
    ): Boolean {
        val params = mutableMapOf<KParameter, Any?>()

        val actor = when (sender) {
            is Player -> dev.mikan.altairkit.api.commands.actors.Player(sender, args.toList())
            is ConsoleCommandSender -> dev.mikan.altairkit.api.commands.actors.Console(sender, args.toList())
            else -> {
                return false
            }
        }

        when (this.sender?.user) {
            null -> {
                return false
            }

            User.CONSOLE -> if (!actor.isConsole()) return false
            User.PLAYER -> if (!actor.isPlayer()) return false
            else -> {}
        }

        if (args.isEmpty() && this.subcommands.isNotEmpty()) return false

        if (args.isNotEmpty() && this.subcommands.contains(args[0])) {
            val subcommand = Cmd.cmdCache[args[0]]
            subcommand?.execute(sender,args[0],args.copyOfRange(1,args.size))
            return false
        }

        /*
        * Since param 1, 2 and 3 in the method will always be
        * this.instance, AltairCMD and Actor in this for loop you will
        * always be sure that i - 3 won't cause exceptions
        * */

        params[onPerform.parameters[0]] = instance
        params[onPerform.parameters[1]] = this
        params[onPerform.parameters[2]] = actor

        /*
        * Wrong parameters will bring to null values, -1
        * or empty strings
        * */

        for (i in onPerform.parameters.indices) {
            if (i < 3) continue
            val param = onPerform.parameters[i]
            when (param.type.classifier) {
                Player::class -> params[param] = Bukkit.getPlayer(args[onPerform.parameters.indexOf(param) - 3])
                String::class -> params[param] = args[onPerform.parameters.indexOf(param) - 3]
                Int::class -> params[param] = if (args[onPerform.parameters.indexOf(param) - 3].isParsableToInt()) args[onPerform.parameters.indexOf(param) - 3].toInt() else -1
                Double::class -> {
                    val arg = args[onPerform.parameters.indexOf(param) - 3]
                    params[param] = when {
                        arg.isParsableToDouble() -> arg.toDouble()
                        arg.isParsableToInt() -> arg.toInt().toDouble()
                        else -> -1.0
                    }

                }
            }
        }

        onPerform.callBy(params)
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String?> {
        val bestMatch = findBestMatch(args.last())

        return if (bestMatch != null && bestMatch.isNotEmpty()) this.subcommands.toMutableList().apply { this.addFirst(bestMatch) }
        else super.tabComplete(sender, alias, args)
    }

    fun findBestMatch(string: String) : String? {
        return this.subcommands.find { it.startsWith(string) }
    }

    override fun toString(): String {
        return this.name + " / " + this.subcommands
    }

}