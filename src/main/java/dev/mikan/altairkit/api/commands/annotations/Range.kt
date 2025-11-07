package dev.mikan.altairkit.api.commands.annotations
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Range(
    val min: Int = Int.MIN_VALUE,
    val max: Int = Int.MAX_VALUE,
    val useMin: Boolean = true,
    val useMax: Boolean = false,
    )
