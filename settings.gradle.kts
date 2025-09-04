pluginManagement {
    plugins {
        kotlin("jvm") version "2.2.0"
    }
}
rootProject.name = "AltairKit"
gradle.rootProject{
    extra["updateRepo"] = true
    extra["copy"] = false
    extra["outputDir"] = "/home/mikan/Desktop/localhosts/kotlin/plugins/"
}

