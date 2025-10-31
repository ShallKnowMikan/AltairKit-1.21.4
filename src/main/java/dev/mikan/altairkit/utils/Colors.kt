package dev.mikan.altairkit.utils

import org.bukkit.Color
object Colors {

    fun fromHSL(hue: Float, saturation: Float, lightness: Float): Color {
        val h = (hue % 360 + 360) % 360
        val s = (saturation / 100f).coerceIn(0f, 1f)
        val l = (lightness / 100f).coerceIn(0f, 1f)

        val c = (1 - kotlin.math.abs(2 * l - 1)) * s
        val x = c * (1 - kotlin.math.abs((h / 60f) % 2 - 1))
        val m = l - c / 2

        val (r1, g1, b1) = when {
            h < 60 -> Triple(c, x, 0f)
            h < 120 -> Triple(x, c, 0f)
            h < 180 -> Triple(0f, c, x)
            h < 240 -> Triple(0f, x, c)
            h < 300 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val r = ((r1 + m) * 255).toInt().coerceIn(0, 255)
        val g = ((g1 + m) * 255).toInt().coerceIn(0, 255)
        val b = ((b1 + m) * 255).toInt().coerceIn(0, 255)

        return Color.fromRGB(r, g, b)
    }
}
