package dev.mikan.altairkit.utils.methods

import dev.mikan.altairkit.AltairKit.Companion.toMessage
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

object PlayerMethods {

    fun Player.sendMessage(msg: String) {
        this.sendMessage { msg.toMessage() }
    }

    fun Player.nearbySphere(radius: Double) : Set<LivingEntity> {
        val box: BoundingBox = this.boundingBox.expand(radius)

        // Gets all the living entities in box of given range
        val nearby: MutableCollection<LivingEntity> = this.world.getNearbyEntities(
            box
        ) { e -> e != null && e is LivingEntity && e != this }.stream()
            .map { e -> e as LivingEntity }
            .toList()

        // filters all the null instances and entities not in spherical range
        nearby.stream().filter { e -> e != null && e.location.distance(this.eyeLocation) <= radius }.toList()

        return nearby.toSet()
    }

    fun Player.nearby(distance: Double) : Set<LivingEntity> {
        val box: BoundingBox = this.boundingBox.expand(distance)

        // Gets all the living entities in box of given range
        val nearby: MutableCollection<LivingEntity> = this.world.getNearbyEntities(
            box
        ) { e -> e != null && e is LivingEntity && e != this }.stream()
            .map { e -> e as LivingEntity }
            .toList()

        return nearby.toSet()
    }

}