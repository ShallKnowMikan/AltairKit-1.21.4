package dev.mikan.altairkit.utils

import dev.mikan.altairkit.api.commands.AltairCMD
import dev.mikan.altairkit.api.commands.CmdClass
import dev.mikan.altairkit.api.commands.annotations.Command
import dev.mikan.altairkit.api.commands.annotations.Complete
import dev.mikan.altairkit.api.commands.annotations.Permission
import dev.mikan.altairkit.api.commands.annotations.Sender
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

        val cmdCache = mutableMapOf<String, AltairCMD>()

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
                for (i in tokens.indices) {
                    val cmdString = tokens[i]
                    val cmd = AltairCMD(cmdString,method,instance,commandAnnotation,completeAnnotation,senderAnnotation,permissionAnnotation)

                    // load cache and updates command map (creating the actual command)
                    cmdCache[cmdString] = cmd
                    getCommandMap().register(cmdString,cmd)

                    if (i + 1 == tokens.size) {
                        // Last iteration
                        // Preventing index out of bound exception
                        break
                    }

                    cmd.subcommands.add(tokens[i+ 1])
                }
            }
            logger.info("Registered commands: $cmdCache")
        }





        @JvmStatic
        fun getCommandMap() : CommandMap{
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