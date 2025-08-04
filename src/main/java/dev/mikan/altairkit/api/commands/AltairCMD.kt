package dev.mikan.altairkit.api.commands

import dev.mikan.altairkit.AltairKit.Companion.isParsableToDouble
import dev.mikan.altairkit.AltairKit.Companion.isParsableToInt
import dev.mikan.altairkit.api.commands.annotations.*
import dev.mikan.altairkit.utils.Tree
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.lang.IndexOutOfBoundsException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class AltairCMD(
    name: String,
    val tree: Tree<AltairCMD>,
    var onPerform: KFunction<*>?,
    private val instance: Any?,
    val command: Command? = null,
    val complete: Complete? = null,
    val sender: Sender? = null,
    val permission: Permission? = null,
) : BukkitCommand(name) {

    override fun execute(
        sender: CommandSender,
        cmdName: String,
        args: Array<out String>
    ): Boolean {
        val params = mutableMapOf<KParameter, Any?>()

        if (args.isNotEmpty() && tree.fetch { cmd -> cmd.name == args[0] } != null) {
            val subCommand = tree.fetch { cmd -> cmd.name == args[0] }
            subCommand?.execute(sender,args[0],args.copyOfRange(1,args.size))
            return false
        }

        val actor = when (sender) {
            is Player -> dev.mikan.altairkit.api.commands.actors.Player(sender, args.toList())
            is ConsoleCommandSender -> dev.mikan.altairkit.api.commands.actors.Console(sender, args.toList())
            else -> {
                return false
            }
        }
        if ((permission != null && permission.blocking) || (permission != null && !actor.hasPermission(permission.permission))) return false
        when (this.sender?.user) {
            null -> return false
            User.CONSOLE -> if (!actor.isConsole()) return false
            User.PLAYER -> if (!actor.isPlayer()) return false
            else -> {}
        }



        if (onPerform == null) return false

        /*
        * Since param 1, 2 and 3 in the method will always be
        * this.instance, AltairCMD and Actor in this for loop you will
        * always be sure that i - 3 won't cause exceptions
        * */

        params[onPerform!!.parameters[0]] = instance
        params[onPerform!!.parameters[1]] = this
        params[onPerform!!.parameters[2]] = actor

        /*
        * Wrong parameters will bring to null values, -1
        * or empty strings
        * if the method requires 3 parameters and only 2
        * have been passed then the last will enter the
        * catch for index out of bounds (args - 3 = invalid index)
        * */

        for (i in onPerform!!.parameters.indices) {
            if (i < 3) continue
            val param = onPerform!!.parameters[i]
            when (param.type.classifier) {
                Player::class -> params[param] = try {
                    Bukkit.getPlayer(args[onPerform!!.parameters.indexOf(param) - 3])
                } catch (_: IndexOutOfBoundsException) { null }
                String::class -> params[param] = try {
                    args[onPerform!!.parameters.indexOf(param) - 3]
                } catch (_: IndexOutOfBoundsException) { "" }
                Int::class -> params[param] =  {
                    val arg = try {
                        args[onPerform!!.parameters.indexOf(param) - 3]
                    } catch (_: IndexOutOfBoundsException) { "-1" }
                    params[param] = when {
                        arg.isParsableToInt() -> arg.toInt()
                        else -> -1
                    }
                }
                Double::class -> {
                    val arg = try {
                        args[onPerform!!.parameters.indexOf(param) - 3]
                    } catch (_: IndexOutOfBoundsException) { "-1" }
                    params[param] = when {
                        arg.isParsableToDouble() -> arg.toDouble()
                        arg.isParsableToInt() -> arg.toInt().toDouble()
                        else -> -1.0
                    }

                }
            }
        }

        onPerform!!.callBy(params)
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String?> {

        // Trying to get last subcommand written, but if space after the command last argument will be ""
        // So I need to get the n - 2 one

        // This block returns all successive subcommands starting from first child of root
        if (args.size > 1) {
            tree.fetch { cmd -> cmd.name == args[args.size - 2]}?.let {
                // Remember to use cmd here to refer to AltairKit current instance !!
                cmd ->
                val completions = mutableListOf<String>()
                tree.search(cmd)?.children?.forEach { child -> completions.add(child.data.name) }

                return completions.ifEmpty {
                    return if (cmd.complete == null || cmd.complete.args.isEmpty()) super.tabComplete(sender, alias, args)
                    else cmd.complete.args.toList()
                }
            }
        }

        // Returns first subcommands from root
        if (args.size == 1) {
            val node = tree.search(this)
            node?: super.tabComplete(sender, alias, args)
            val possibleCommands = node!!.children.filter { node -> node.data.name.startsWith(args.getOrElse(0){""}) }

            val possibleCmdList = mutableListOf<String>()
            possibleCommands.forEach { node -> possibleCmdList.add(node.data.name) }
            return possibleCmdList
        }

        return super.tabComplete(sender, alias, args)
    }


    override fun toString(): String {
        return this.name
    }

}