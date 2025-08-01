pluginManagement {
    plugins {
        kotlin("jvm") version "2.2.0"
    }
}
rootProject.name = "Altair"
gradle.rootProject{
    extra["updateRepo"] = true
    extra["copy"] = true
    extra["outputDir"] = "/home/mikan/Desktop/localhosts/1_8/plugins"
}

