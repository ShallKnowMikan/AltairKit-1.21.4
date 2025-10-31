package dev.mikan.altairkit.api.commands

import dev.mikan.altairkit.AltairKit.Companion.isParsableToDouble
import dev.mikan.altairkit.AltairKit.Companion.isParsableToInt
import dev.mikan.altairkit.api.commands.annotations.*
import dev.mikan.altairkit.utils.Logger
import dev.mikan.altairkit.utils.Tree
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import java.lang.IndexOutOfBoundsException
import java.util.Arrays
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
    val completions: MutableList<String> = mutableListOf()
) : BukkitCommand(name) {

    init {
        if (complete != null)
            completions.addAll(complete.value)
    }

    override fun execute(
        sender: CommandSender,
        cmdName: String,
        arguments: Array<out String>
    ): Boolean {
        val params = mutableMapOf<KParameter, Any?>()

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


        /*
        * Since param 1 and 2 in the method will always be
        * this.instance and Actor in this for loop you will
        * always be sure that i - 2 won't cause exceptions
        * */

        params[onPerform!!.parameters[0]] = instance
        params[onPerform!!.parameters[1]] = actor

        val paramsOffset = 2

        var argsPointer = 0
        var paramsPointer = 0 + paramsOffset

        /*
        * First it assigns as many arg as possible, considering @Default annotation
        * then, if are required more parameters than provided args, it will
        * automatically set the remaining params with one of those: null | "" | -1
        * */

        while (argsPointer < arguments.size && paramsPointer < onPerform!!.parameters.size) {
            val param = onPerform!!.parameters[paramsPointer]
            when (onPerform!!.parameters[paramsPointer]) {
                Player::class -> {
                    val player = Bukkit.getPlayer(arguments[argsPointer])
                    if (player == null && param.hasAnnotation<Default>() && actor.isPlayer()) {
                        params[param] = actor.asPlayer()

                        paramsPointer ++
                        continue
                    }
                    else {
                        paramsPointer ++
                        argsPointer ++
                        params[param] = null
                        continue
                    }
                }

                Double::class -> {
                    params[param] = if (arguments[argsPointer].isParsableToDouble()) arguments[argsPointer].toDouble()
                    else if (arguments[argsPointer].isParsableToInt()) arguments[argsPointer].toInt()
                    else -1.0
                }

                Int::class -> {
                    params[param] =  if (arguments[argsPointer].isParsableToInt()) arguments[argsPointer].toInt()
                    else -1
                }

                String::class -> {params[param] = arguments[argsPointer].toString()}
            }
            paramsPointer ++
            argsPointer ++
        }

//        Logger.warning(" Param values til now: ${params.values} | argIndex: $argsPointer of ${arguments.size} | paramIndex: $paramsPointer of ${onPerform!!.parameters.size}")


        if (params.size < onPerform!!.parameters.size) {
//            Logger.warning(" Adjusting params.")
//            Logger.warning(" from: ${params.size} to ${onPerform!!.parameters.size}")
            for (i in params.size until onPerform!!.parameters.size) {
                val param = onPerform!!.parameters[i]
                Logger.warning(" Adjusting: ${param.type.classifier}")
                params[param] = when (param.type.classifier) {
                    Int::class -> -1
                    Double::class -> -1
                    Float::class -> -1
                    Long::class -> -1
                    Player::class -> if (param.hasAnnotation<Default>() && actor.isPlayer()) actor.asPlayer() else null
                    String::class -> ""
                    else -> null
                }
            }
//            Logger.warning(" adjusted params: ${params.values}")
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
                    return if (cmd.completions.isEmpty()) super.tabComplete(sender, alias, args)
                    else cmd.completions
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
                if (this.completions.isEmpty()) return super.tabComplete(sender, alias, args)
                return this.completions
            }
        }

        return super.tabComplete(sender, alias, args)
    }

    private fun assignParam(params: MutableMap<KParameter, Any?>,onPerform: KFunction<*>, args: List<String>, paramPointer: Int, argPointer : Int) {
        val param = onPerform.parameters[paramPointer]

        when (param.type.classifier) {
            Player::class -> {
                val player = Bukkit.getPlayer(args[argPointer])
                if (param.hasAnnotation<Default>() && player == null) {

                }

            }
        }
    }


    override fun toString(): String {
        return this.name
    }

}