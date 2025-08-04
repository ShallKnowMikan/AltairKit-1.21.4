package dev.mikan.altairkit.api.commands.annotations
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Complete(vararg val value: String)
