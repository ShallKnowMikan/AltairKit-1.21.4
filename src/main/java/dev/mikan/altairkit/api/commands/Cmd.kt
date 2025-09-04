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

                if (commandAnnotation?.value == null || commandAnnotation.value.isEmpty()) continue


                val completeAnnotation = method.findAnnotation<Complete>()
                val permissionAnnotation = method.findAnnotation<Permission>()
                val senderAnnotation = method.findAnnotation<Sender>()




                // load subcommands and cache
                val tokens = commandAnnotation.value.split(" ")
                val cmdTree = cache[tokens[0]]?: Tree()
                for (i in tokens.indices) {
                    val cmdString = tokens[i]
                    val isLastToken= (i + 1 == tokens.size)
                    val cmd = AltairCMD(cmdString,cmdTree,
                        if (isLastToken) method else null,
                        instance,
                        if (isLastToken) commandAnnotation else null,
                        if (isLastToken) completeAnnotation else null,
                        if (isLastToken) senderAnnotation else null,
                        if (isLastToken) permissionAnnotation else null)

                    val isRoot = i == 0

                    // In order to register subcommands and then still allow
                    // method assignation to root command
                    if (isRoot && tokens.size == 1 && cmdTree.root != null) {
                        cmdTree.root!!.data.onPerform = method
                        continue
                    } else if (isRoot) {
                        // Inner check since if it is root it still has to use continue
                        if (cmdTree.root == null) {
                            cmdTree.setRoot(cmd)
                            getCommandMap().register(cmdString,cmd)
                        }
                        continue
                    }

                    // Add new subcommand as node to parent in tree

                    val parentName = tokens[i - 1]
                    val parent = cmdTree.fetch { cmd -> cmd.name == parentName}

                    // Uses the condition passed by lambda to detect if there is an existing instance already
                    // without lambda it will do a raw check between instances with == operator
                    cmdTree.insert(parent!!,cmd) { inner -> inner.name == cmd.name }

                }
                cache.putIfAbsent(cmdTree.root!!.data.name,cmdTree)
            }
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

        /*
        * Will return the first match with the name.
        * If tree contains a cmd with the same name
        * the returned instance might be wrong.
        *
        * TODO: add an id to commands in order to recognize them by id
        * */
        @JvmStatic
        fun getCMD(rootName: String,cmdName: String): AltairCMD? {
            val tree = cache[rootName]
            tree?.let { tree ->
                return tree.fetch { cmd -> cmd.name == cmdName }
            }

            return null
        }
    }

}