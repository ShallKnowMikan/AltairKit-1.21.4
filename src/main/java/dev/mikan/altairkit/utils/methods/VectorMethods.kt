package dev.mikan.altairkit.utils.methods

import dev.mikan.altairkit.utils.Logger
import org.bukkit.util.Vector
import org.checkerframework.checker.units.qual.degrees
import java.lang.Math.toRadians
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object VectorMethods {

    /*
    * Returns a vector with the same length as the current one but its
    * tip will be shifted of:
    * @param alongAxisAngle: the angle between our 2 vectors
    * @param aroundAxisAngle: the rotation angle around this instance's vector
    * those are not in radians, you'll need to pass them in degrees
    * */
    fun Vector.angle(alongAxisAngle: Double,aroundAxisAngle: Double) : Vector{
        val length = this.length()

        val theta = toRadians(alongAxisAngle)
        val phi = toRadians(aroundAxisAngle)

        val vNorm = this.normalize()

        val o1 = this.orthogonal()
        val o2 = vNorm.clone().crossProduct(o1)

        // Just created orthogonal floor with
        // y -> vNorm
        // x -> o2
        // z -> o1

        /*
        * In order to get the result vector (let's call it U)
        * I'm using this formula:
        * U = |U|cos(θ) × v̂ + |U|sin(θ) × (cos(φ)p₁ + sin(φ)p₂)
        *
        * that simplifies to:
        *
        * U = |U| × [cos(θ)v̂ + sin(θ)(cos(φ)p₁ + sin(φ)p₂)]
        * */

        val cosTheta = cos(theta)
        val cosPhi = cos(phi)

        val sinTheta = sin(theta)
        val sinPhi = sin(phi)

        val o1Component = o1.clone().normalize().multiply(cosPhi)
        val o2Component = o2.clone().normalize().multiply(sinPhi)
        val orthogonalComponent = o1Component.clone().add(o2Component).multiply(sinTheta)

        val result = orthogonalComponent.add(vNorm.clone().multiply(cosTheta)).multiply(length)

        return result
    }


    fun Vector.orthogonal(): Vector {
        val perpendicular = if (abs(this.y) > 0.9) {
            Vector(1.0, 0.0, 0.0).crossProduct(this)
        } else {
            Vector(0.0, 1.0, 0.0).crossProduct(this)
        }
        return perpendicular.normalize()
    }

}