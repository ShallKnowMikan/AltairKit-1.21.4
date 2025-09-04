package dev.mikan.altairkit

import dev.mikan.altairkit.api.commands.AltairCMD
import dev.mikan.altairkit.api.commands.Cmd
import dev.mikan.altairkit.api.commands.CmdClass
import dev.mikan.altairkit.utils.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Consumer


class AltairKit : JavaPlugin() {


    override fun onEnable() {
        // Testing only

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
        fun registerCommands(instance: CmdClass){
            Cmd.registerCommands(instance)
        }

        @JvmStatic
        fun addCompletions(cmd: AltairCMD?,list: List<String>) {
            cmd?.completions?.addAll(list)
        }

        @JvmStatic
        fun colorize(s: String?): Component {
            val msg = s?: ""
            return ChatColor.translateAlternateColorCodes('&', msg).toMessage()
        }

        @JvmStatic
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
        fun String.toMessage(): Component {
            return miniMessage.deserialize(this)
        }
    }


}
