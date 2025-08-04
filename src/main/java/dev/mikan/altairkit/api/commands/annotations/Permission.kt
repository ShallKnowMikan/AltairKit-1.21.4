package dev.mikan.altairkit.api.commands.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Permission(val value: String, val blocking: Boolean = true)
