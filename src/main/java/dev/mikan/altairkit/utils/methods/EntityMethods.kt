package dev.mikan.altairkit.utils.methods

import org.bukkit.entity.Display
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.util.BoundingBox

object EntityMethods {
    fun Entity.nearbySphere(radius: Double) : Set<LivingEntity> {
        val box: BoundingBox = this.boundingBox.expand(radius)

        // Gets all the living entities in box of given range
        val nearby: MutableCollection<LivingEntity> = this.world.getNearbyEntities(
            box
        ) { e -> e != null && e is LivingEntity && e != this }.stream()
            .map { e -> e as LivingEntity }
            .toList()

        // filters all the null instances and entities not in spherical range
        nearby.stream().filter { e -> e != null && e.location.distance(this.location) <= radius }.toList()

        return nearby.toSet()
    }

    fun Entity.nearby(distance: Double) : Set<LivingEntity> {
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