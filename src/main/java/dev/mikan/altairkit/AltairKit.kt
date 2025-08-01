package dev.mikan.altairkit

import dev.mikan.altairkit.test.Commands
import dev.mikan.altairkit.utils.Cmd
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Consumer


class AltairKit : JavaPlugin() {


    override fun onEnable() {
        // Testing only

        Cmd.registerCommands(Commands())

    }





    companion object {

        @JvmStatic
        fun head(name: String): ItemStack {
            val skull = ItemStack(Material.PLAYER_HEAD)
            val meta = skull.itemMeta as SkullMeta

            meta.playerProfile = Bukkit.createProfile(name)

            skull.setItemMeta(meta)
            return skull
        }

        @JvmStatic
        fun Player.head(): ItemStack {
            val skull = ItemStack(Material.PLAYER_HEAD)
            val meta = skull.itemMeta as SkullMeta

            meta.playerProfile = this.playerProfile

            skull.setItemMeta(meta)
            return skull
        }

        @JvmStatic
        fun head(uuid: UUID): ItemStack {
            val skull = ItemStack(Material.PLAYER_HEAD)
            val meta = skull.itemMeta as SkullMeta

            meta.playerProfile = Bukkit.createProfile(uuid)

            skull.setItemMeta(meta)
            return skull
        }


        @JvmStatic
        fun colorize(s: String): String {
            return ChatColor.translateAlternateColorCodes('&', s)
        }

        fun colorize(lines: MutableList<String?>): MutableList<String?> {
            val colorizedList: MutableList<String?> = ArrayList<String?>()
            lines.forEach(Consumer { line: String? ->
                colorizedList.add(
                    ChatColor.translateAlternateColorCodes(
                        '&',
                        line!!
                    )
                )
            })
            return colorizedList
        }

        fun String.isNumeric(): Boolean {
            return toIntOrNull() != null || toDoubleOrNull() != null
        }

        fun String.isParsableToInt(): Boolean {
            return toIntOrNull() != null
        }

        fun String.isParsableToDouble(): Boolean {
            return toDoubleOrNull() != null
        }

        val miniMessage = MiniMessage.builder().build()
        fun String.colorize(): Component = miniMessage.deserialize(this)
    }
}
