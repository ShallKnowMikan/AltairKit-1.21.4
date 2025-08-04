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
import kotlin.reflect.full.hasAnnotation

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
        arguments: Array<out String>
    ): Boolean {
        val params = mutableMapOf<KParameter, Any?>()
        val logger = Bukkit.getLogger()

        if (arguments.isNotEmpty() && tree.fetch { cmd -> cmd.name == arguments[0] } != null) {
            val subCommand = tree.fetch { cmd -> cmd.name == arguments[0] }

            subCommand?.execute(sender,arguments[0],arguments.copyOfRange(1,arguments.size))
            return false
        }

        val actor = when (sender) {
            is Player -> dev.mikan.altairkit.api.commands.actors.Player(sender, arguments.toList())
            is ConsoleCommandSender -> dev.mikan.altairkit.api.commands.actors.Console(sender, arguments.toList())
            else -> {
                return false
            }
        }

        if (permission != null && permission.blocking && !actor.hasPermission(permission.value)) return false

        when (this.sender?.value) {
            null -> return false
            User.CONSOLE -> if (!actor.isConsole()) return false
            User.PLAYER -> if (!actor.isPlayer()) return false
            else -> {}
        }


        if (onPerform == null) return false

        /*
        * Since param 1 and 2 in the method will always be
        * this.instance and Actor in this for loop you will
        * always be sure that i - 2 won't cause exceptions
        * */

        params[onPerform!!.parameters[0]] = instance
        params[onPerform!!.parameters[1]] = actor

        /*
        * Wrong parameters will bring to null values, -1
        * or empty strings
        * if the method requires n(parmsOffset) parameters and only 1
        * has been passed then the last will enter the
        * catch for index out of bounds (args - parmsOffset = invalid index)
        * */


        /*
        * Here I sum them (parmsOffset + defaultParams) in order to get
        * how much positions I have to scale from args
        *
        * default params is the amount of time a parameter with @Default
        * annotation has been encountered
        * */
        val paramsOffset = 2

        // If args ar more than expected here they'll be cut into
        // actor#args


        val stack = ArrayDeque<String> ()

        val args = ArrayDeque<String> ().apply { this.addAll(arguments) }

        /*
        * Cannot remove based on the total number of default params here
        * because I still don't know if those defaults are "called"
        * */
        val paramsTotal = onPerform!!.parameters.size

        while (args.size + paramsOffset > paramsTotal){
            stack.addLast(args.removeLast())
        }

        actor.args.toMutableList().addAll(stack)

        for (i in onPerform!!.parameters.indices) {
            if (i < 2) continue
            val param = onPerform!!.parameters[i]

            /*
            * @Default annotation applies to player only
            * */
            val defaultParam = param.hasAnnotation<Default>()

            val argIndex = onPerform!!.parameters.indexOf(param) - paramsOffset


            when (param.type.classifier) {
                Player::class -> params[param] =  try {
                    val player = Bukkit.getPlayer(args[argIndex])
                    player ?: if (defaultParam) {
                        /*
                        * In order to avoid troubles with @Default parameters and indexes error, I'm adding
                        * one element while looping at this conditions
                        * */
                        args.addFirst("X")
                        actor.asPlayer()
                    }
                    else null
                } catch (_: IndexOutOfBoundsException) {
                    null
                }
                String::class -> params[param] = try {
                    args[argIndex]
                } catch (_: IndexOutOfBoundsException) { "" }
                Int::class -> params[param] =  {
                    val arg = try {
                        args[argIndex]
                    } catch (_: IndexOutOfBoundsException) { "-1" }
                    params[param] = when {
                        arg.isParsableToInt() -> arg.toInt()
                        else -> -1
                    }
                }
                Double::class -> {
                    val arg = try {
                        args[argIndex]
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
                    return if (cmd.complete == null || cmd.complete.value.isEmpty()) super.tabComplete(sender, alias, args)
                    else cmd.complete.value.toList()
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
            return possibleCmdList.ifEmpty {
                if (this.complete == null) return super.tabComplete(sender, alias, args)
                return this.complete.value.toList().ifEmpty { super.tabComplete(sender, alias, args) }
            }
        }

        return super.tabComplete(sender, alias, args)
    }


    override fun toString(): String {
        return this.name
    }

}