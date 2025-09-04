package dev.mikan.altairkit.utils

import org.bukkit.Color

object Colors {

    fun fromHSL(hue: Float, saturation: Float, lightness: Float): Color {
        // Normalizzazione dei valori
        val normalizedHue = hue.mod(360f).let { if (it < 0) it + 360 else it }
        val normalizedSaturation = saturation.coerceIn(0f, 1f)
        val normalizedLightness = lightness.coerceIn(0f, 1f)

        val c = (1 - kotlin.math.abs(2 * normalizedLightness - 1)) * normalizedSaturation
        val x = c * (1 - kotlin.math.abs((normalizedHue / 60).mod(2F) - 1))
        val m = normalizedLightness - c / 2

        val (r, g, b) = when {
            normalizedHue < 60 -> Triple(c, x, 0f)
            normalizedHue < 120 -> Triple(x, c, 0f)
            normalizedHue < 180 -> Triple(0f, c, x)
            normalizedHue < 240 -> Triple(0f, x, c)
            normalizedHue < 300 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        return Color.fromRGB(
            ((r + m) * 255).toInt(),
            ((g + m) * 255).toInt(),
            ((b + m) * 255).toInt()
        )
    }
}