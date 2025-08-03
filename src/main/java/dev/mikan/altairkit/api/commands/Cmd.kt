package dev.mikan.altairkit.api.commands

import dev.mikan.altairkit.api.commands.annotations.Command
import dev.mikan.altairkit.api.commands.annotations.Complete
import dev.mikan.altairkit.api.commands.annotations.Permission
import dev.mikan.altairkit.api.commands.annotations.Sender
import dev.mikan.altairkit.utils.Tree
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import java.lang.reflect.Field
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class Cmd {

    companion object {

        val logger = Bukkit.getLogger()

        // root name -> tree
        val cache = mutableMapOf<String,Tree<AltairCMD>>()


        @JvmStatic fun registerCommands(instance: CmdClass){
            for (method in instance::class.declaredFunctions) {

                if (!method.hasAnnotation<Command>()) continue

                if (method.visibility != KVisibility.PUBLIC) continue

                val commandAnnotation = method.findAnnotation<Command>()

                if (commandAnnotation?.cmd == null || commandAnnotation.cmd.isEmpty()) continue


                val completeAnnotation = method.findAnnotation<Complete>()
                val permissionAnnotation = method.findAnnotation<Permission>()
                val senderAnnotation = method.findAnnotation<Sender>()




                // load subcommands and cache
                val tokens = commandAnnotation.cmd.split(" ")
                logger.info { "Tokens: $tokens" }
                val cmdTree = cache[tokens[0]]?: Tree()
                for (i in tokens.indices) {
                    val cmdString = tokens[i]
                    val cmd = AltairCMD(cmdString,cmdTree,if (i + 1 == tokens.size) method else null,instance,commandAnnotation,completeAnnotation,senderAnnotation,permissionAnnotation)

                    val isRoot = i == 0

                    // In order to register subcommands and then still allow
                    // method assignation to root command
                    if (isRoot && tokens.size == 1 && cmdTree.root != null) {
                        cmdTree.root!!.data.onPerform = method
                        continue
                    } else if (isRoot) {
                        // Inner check since if it is root it still has to use continue
                        if (cmdTree.root == null) {
                            logger.info { "Root: $cmd" }
                            cmdTree.setRoot(cmd)
                            getCommandMap().register(cmdString,cmd)
                        }
                        continue
                    }

                    // Add new subcommand as node to parent in tree

                    val parentName = tokens[i - 1]
                    val parent = cmdTree.fetch { cmd -> cmd.name == parentName}
                    logger.info { "Parent: $parent" }
                    logger.info { "Cmd: $cmd" }
                    // Uses the condition passed by lambda to detect if there is an existing instance already
                    // without lambda it will do a raw check between instances with == operator
                    cmdTree.insert(parent!!,cmd) { inner -> inner.name == cmd.name }

                    getCommandMap().register(cmdString,cmd)

                    if (i + 1 == tokens.size) {
                        // Last iteration
                        // Preventing index out of bound exception
                        continue
                    }

                }
                cache.putIfAbsent(cmdTree.root!!.data.name,cmdTree)
            }
            logger.info("Registered commands: ${cache.values}")
        }





        @JvmStatic
        fun getCommandMap() : CommandMap {
            return try {
                val mapField: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
                mapField.isAccessible = true
                mapField.get(Bukkit.getServer()) as CommandMap
            } catch (e: Exception) {
                throw RuntimeException("Unable to access the command map from Bukkit server.", e)
            }
        }
    }

}