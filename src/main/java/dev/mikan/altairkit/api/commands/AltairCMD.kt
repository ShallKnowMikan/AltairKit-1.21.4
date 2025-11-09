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
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class AltairCMD(
    name: String,
    val tree: Tree<AltairCMD>,
    var onPerform: KFunction<*>,
    private val instance: Any?,
    val command: Command? = null,
    val sender: Sender? = null,
    val permission: Permission? = null,
    val completions: MutableList<MutableSet<String>> = mutableListOf(),
) : BukkitCommand(name) {

    init {
        // Remember that param 0 is instance
        // and param 1 is Actor, so I do not need them
        val size = onPerform.parameters.size
        for (index in 2 until size) {
            val hasAnnotation = onPerform.parameters[index].hasAnnotation<Complete>()
            val completeAnnotation = onPerform.parameters[index].findAnnotation<Complete>()
            if (completeAnnotation != null)
                completions.add(completeAnnotation.value.toMutableSet())
            else completions.add(mutableSetOf())
        }


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

        params[onPerform.parameters[0]] = instance
        params[onPerform.parameters[1]] = actor

        val paramsOffset = 2

        var argsPointer = 0
        var paramsPointer = 0 + paramsOffset

        /*
        * First it assigns as many arg as possible, considering @Default annotation
        * then, if are required more parameters than provided args, it will
        * automatically set the remaining params with one of those: null | "" | -1
        *
        * Same goes for @Range annotation
        * */
        while (argsPointer < arguments.size && paramsPointer < onPerform.parameters.size) {
            val param = onPerform.parameters[paramsPointer]
            val hasRangeAnnotation = param.hasAnnotation<Range>()
            val hasDefaultAnnotation = param.hasAnnotation<Default>()

            when (param.type.classifier) {
                Player::class -> {
                    val player = Bukkit.getPlayer(arguments[argsPointer])
                    if (player == null && hasDefaultAnnotation && actor.isPlayer()) {
                        params[param] = actor.asPlayer()

                        paramsPointer ++
                        continue
                    } else if (player != null) {
                        params[param] = player
                        paramsPointer ++
                        argsPointer ++
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

                    val isParsable = arguments[argsPointer].isParsableToDouble()

                    if (hasRangeAnnotation) {
                        val annotation = param.findAnnotation<Range>()
                        val min = annotation!!.min
                        val max = annotation.max
                        var value = if (arguments[argsPointer].isParsableToDouble()) {
                            arguments[argsPointer].toDouble()
                        } else if (arguments[argsPointer].isParsableToInt()) {arguments[argsPointer].toInt()}
                        else Double.MIN_VALUE

                        params[param] = max(annotation.min.toDouble(),min(value.toDouble(),annotation.max.toDouble()))

                        paramsPointer ++
                        continue
                    }

                    if (hasDefaultAnnotation && !isParsable) {
                        val annotation = param.findAnnotation<Default>()
                        val value = annotation?.value?: ""
                        params[param] = if (value.isParsableToDouble()) value.toDouble()
                        else if (value.isParsableToInt()) value.toInt()
                        else -1
                         paramsPointer ++
                         continue
                    }

                    params[param] = if (arguments[argsPointer].isParsableToDouble()) arguments[argsPointer].toDouble()
                    else if (arguments[argsPointer].isParsableToInt()) arguments[argsPointer].toInt()
                    else -1.0
                }

                Int::class -> {

                    val isParsable = arguments[argsPointer].isParsableToDouble()

                    if (hasRangeAnnotation) {
                        val annotation = param.findAnnotation<Range>()
                        val min = annotation!!.min
                        val max = annotation.max
                        var value = if (arguments[argsPointer].isParsableToInt()) {arguments[argsPointer].toInt()}
                        else Int.MIN_VALUE

                        params[param] = max(annotation.min,min(value,annotation.max))
                        paramsPointer ++
                        continue
                    }

                    if (hasDefaultAnnotation && !isParsable) {
                        val annotation = param.findAnnotation<Default>()
                        val value = annotation?.value?: ""

                        params[param] = if (value.isParsableToInt()) value.toInt()
                        else -1
                        paramsPointer ++
                        continue
                    }

                    params[param] =  if (arguments[argsPointer].isParsableToInt()) arguments[argsPointer].toInt()
                    else -1
                }

                String::class -> {
                    if (hasDefaultAnnotation ) {
                        val annotation = param.findAnnotation<Default>()
                        params[param] =  annotation?.value?: ""
                        paramsPointer ++
                        continue
                    }
                    params[param] = arguments[argsPointer].toString()
                }
            }
            paramsPointer ++
            argsPointer ++
        }



        if (params.size < onPerform.parameters.size) {
            for (i in params.size until onPerform.parameters.size) {
                val param = onPerform.parameters[i]
                val hasDefaultAnnotation = param.hasAnnotation<Default>()
                val hasRangeAnnotation = param.hasAnnotation<Range>()
                val defaultAnnotation = param.findAnnotation<Default>()
                val rangeAnnotation = param.findAnnotation<Range>()
                params[param] = when (param.type.classifier) {
                    Int::class -> {
                        val value = if (hasDefaultAnnotation) {
                            if (defaultAnnotation == null) -1 else this.toInt(defaultAnnotation.value)
                        } else -1
                        if (hasRangeAnnotation) {
                            if (rangeAnnotation == null) value else max(rangeAnnotation.min,min(value,rangeAnnotation.max))
                        } else value

                    }
                    Double::class -> {
                        val value = if (hasDefaultAnnotation) {
                            if (defaultAnnotation == null) -1.0 else this.toDouble(defaultAnnotation.value)
                        } else -1.0
                        if (hasRangeAnnotation) {
                            if (rangeAnnotation == null) value else max(rangeAnnotation.min.toDouble(),min(value,rangeAnnotation.max.toDouble()))
                        } else value
                    }
//                    Float::class -> if (hasDefaultAnnotation) {
//                        if (defaultAnnotation == null) -1F else this.toFloat(defaultAnnotation.value)
//                    } else -1F
//                    Long::class -> if (hasDefaultAnnotation) {
//                        if (defaultAnnotation == null) -1L else this.toLong(defaultAnnotation.value)
//                    } else -1L
                    Player::class -> if (hasDefaultAnnotation && actor.isPlayer()) actor.asPlayer() else null
                    String::class -> ""
                    else -> null
                }
            }
        }

        onPerform.callBy(params)
        return true

    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String> {

        val cmd = this.fetchCommand(sender, alias, args)
        val cmdCompletions = this.fetchCMDCompletions(cmd,args)


        return if (cmd != null && cmdCompletions.isNotEmpty()) {
            cmdCompletions
        } else {
            return processCompletions(args, { super.tabComplete(sender, alias, args) }).toList()
        }

    }

    private fun fetchCMDCompletions(cmd: AltairCMD?,args: Array<out String>) : List<String>{
        cmd?: return emptyList()
        val node = tree.search(cmd) ?: return emptyList()

        val possibleCmdList = mutableListOf<String>()
        node.children.forEach { node -> possibleCmdList.add(node.data.name) }
        return possibleCmdList
    }

    private fun fetchCommand(sender: CommandSender, alias: String, args: Array<out String>) : AltairCMD? {
        // Trying to get last subcommand written, but if space after the command last argument will be ""
        // So I need to get the n - 2 one

        // This block returns all successive subcommands starting from first child of root
        return if (args.size > 1) tree.fetch { cmd -> cmd.name == args[args.size - 2]}
        else this
    }


    private fun processCompletions(fullArgs: Array<out String>,default: () -> List<String>) : List<String> {
        var cmd: AltairCMD = this

        for (arg in fullArgs) {
            val tmp = Cmd.getCMD(this.name,arg)
            if (tmp == null) break
            cmd = tmp
        }

        // The only case in which it is equal to this
        // is when the command is root and has no subcmds
        val args = if (cmd != this) {
            val cmdIndex = fullArgs.indexOfLast { it.lowercase() == cmd.name.lowercase() }
            fullArgs.toList().subList(cmdIndex + 1,fullArgs.size)
        } else {
            fullArgs.toList()
        }

        val params = cmd.onPerform.parameters
        val argNumber = args.size - 1
        return try {
            val param = params[argNumber + 2]
            val type = params[argNumber + 2].type.classifier
            val completions: List<String> = cmd.completions[argNumber].toList()
            val isEmpty = completions.isEmpty()

            when (type) {
                Player::class -> {
                    if (isEmpty) default()
                    else completions
                }

                Int::class -> {
                    if (isEmpty) listOf("<${param.name}>")
                    else completions
                }

                Double::class -> {
                    if (isEmpty) listOf("<${param.name}>")
                    else completions
                }

                String::class -> {
                    if (isEmpty) listOf("<${param.name}>")
                    else completions
                }

                else -> listOf<String>("MemoriaMC!")
            }

        } catch (e: IndexOutOfBoundsException) {listOf<String>("")}
    }

    private fun toLong(string: String): Long {
        if (string.isBlank()) return -1L
        return string.toLong()
    }

    private fun toInt(string: String): Int {
        if (string.isBlank()) return -1
        return string.toInt()
    }

    private fun toDouble(string: String): Double {
        if (string.isBlank()) return -1.0
        return string.toDouble()
    }

    private fun toFloat(string: String): Float {
        if (string.isBlank()) return -1.0F
        return string.toFloat()
    }

    override fun toString(): String {
        return this.name
    }

}