package dev.mikan.altairkit.utils

import dev.mikan.altairkit.AltairKit.Companion.colorize
import dev.mikan.altairkit.AltairKit.Companion.toMessage
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class ItemBuilder {

    val item: ItemStack
    var meta: ItemMeta? = null

    constructor(material: Material) {
        this.item = ItemStack(material)
        this.meta = item.itemMeta
    }

    constructor(item: ItemStack) {
        this.item = item
    }

    fun setName(name: String) : ItemBuilder {
        meta?.displayName(name.toMessage())
        return this
    }

    fun setLore(lore: List<String>) : ItemBuilder {
        val toComponents = mutableListOf<Component>()
        lore.forEach { line -> toComponents.add(line.toMessage())}
        meta?.lore(toComponents)
        return this
    }

    fun setMaterial(material: Material) : ItemBuilder {
        item.type = material
        return this
    }

    fun setPersistentData(key: String, data: Any) : ItemBuilder{
        val namespacedKey = NamespacedKey.minecraft(key)
        val meta = this.meta?: this.item.itemMeta
        val container = meta.persistentDataContainer

        when (data) {
            is String -> container.set(namespacedKey, PersistentDataType.STRING, data)
            is Int -> container.set(namespacedKey, PersistentDataType.INTEGER, data)
            is Double -> container.set(namespacedKey, PersistentDataType.DOUBLE, data)
            is Boolean -> container.set(namespacedKey, PersistentDataType.BYTE, if (data) 1 else 0)
            is Long -> container.set(namespacedKey, PersistentDataType.LONG, data)
            is ByteArray -> container.set(namespacedKey, PersistentDataType.BYTE_ARRAY, data)
            else -> error("Type not supported: ${data::class}")
        }

        item.itemMeta = meta
        return this
    }



    fun toItem() : ItemStack{
        this.item.itemMeta = this.meta
        return this.item
    }
}