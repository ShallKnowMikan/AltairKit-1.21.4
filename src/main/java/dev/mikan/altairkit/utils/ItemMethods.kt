package dev.mikan.altairkit.utils

import dev.mikan.altairkit.AltairKit
import dev.mikan.altairkit.AltairKit.Companion.toMessage
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemMethods {

    fun ItemStack.getName() : String {
        val meta = this.itemMeta

        val name = meta.displayName()?: return ""

        return AltairKit.miniMessage.serialize(name)
    }

    fun ItemStack.getLore() : List<String> {
        val meta = this.itemMeta

        val lore = meta.lore()?: return listOf()

        val list = mutableListOf<String>()

        lore.forEach { line -> list.add(AltairKit.miniMessage.serialize(line)) }

        return list
    }

    fun ItemStack.setName(name: String) : ItemStack {
        val meta = this.itemMeta
        meta.displayName(name.toMessage())
        this.itemMeta = meta
        return this
    }

    fun ItemStack.setLore(lore: List<String>) : ItemStack{
        val toComponents = mutableListOf<Component>()
        lore.forEach { line -> toComponents.add(line.toMessage())}
        val meta = this.itemMeta
        meta.lore(toComponents)
        this.itemMeta = meta
        return this
    }
    fun ItemStack.setMaterial(material: Material) : ItemStack {
        this.type = material
        return this
    }

    fun ItemStack.hasPersistentData(key: String, type: PersistentDataType<*, *>): Boolean {
        val namespacedKey = NamespacedKey.minecraft(key)
        val meta = this.itemMeta?: return false
        val container = meta.persistentDataContainer

        return when (type) {
            PersistentDataType.STRING -> container.has(namespacedKey, PersistentDataType.STRING)
            PersistentDataType.INTEGER -> container.has(namespacedKey, PersistentDataType.INTEGER)
            PersistentDataType.DOUBLE -> container.has(namespacedKey, PersistentDataType.DOUBLE)
            PersistentDataType.BYTE -> container.has(namespacedKey, PersistentDataType.BYTE)
            PersistentDataType.LONG -> container.has(namespacedKey, PersistentDataType.LONG)
            PersistentDataType.BYTE_ARRAY -> container.has(namespacedKey, PersistentDataType.BYTE_ARRAY)
            else -> false
        }
    }

    fun ItemStack.setPersistentData(key: String, data: Any) {
        val namespacedKey = NamespacedKey.minecraft(key)
        val meta = this.itemMeta
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

        this.itemMeta = meta
    }

    fun ItemStack.getPersistentData(key: String, type: PersistentDataType<*, *>): Any? {
        val namespacedKey = NamespacedKey.minecraft(key)
        val meta = this.itemMeta?: return null
        val container = meta.persistentDataContainer

        return when (type) {
            PersistentDataType.STRING -> container.get(namespacedKey, PersistentDataType.STRING)
            PersistentDataType.INTEGER -> container.get(namespacedKey, PersistentDataType.INTEGER)
            PersistentDataType.DOUBLE -> container.get(namespacedKey, PersistentDataType.DOUBLE)
            PersistentDataType.BYTE -> container.get(namespacedKey, PersistentDataType.BYTE)
            PersistentDataType.LONG -> container.get(namespacedKey, PersistentDataType.LONG)
            PersistentDataType.BYTE_ARRAY -> container.get(namespacedKey, PersistentDataType.BYTE_ARRAY)
            else -> null
        }
    }
}