plugins {
    id("java")
    id ("com.gradleup.shadow") version "8.3.0"
    kotlin("jvm") version "1.9.22"
}

group = "dev.mikan"
version = "1.21.4"

val outputDir = file(project.extra["outputDir"] as String)
val copyBoolean = project.extra["copy"] as Boolean
val updateBoolean = project.extra["updateRepo"] as Boolean

tasks.register<Copy>("copy"){
    dependsOn(tasks.named("jar"))
    from(tasks.named("jar").get().outputs.files)
    into(outputDir)
}

tasks.register<Exec>("updateRepo"){
    workingDir = file("/home/mikan/IdeaProjects/AltairKit-1.21.4/build/libs/")
    commandLine(
        "/usr/bin/mvn",
        "install:install-file",
        "-Dfile=AltairKit-1.21.4.jar",
        "-DgroupId=dev.mikan",
        "-DartifactId=AltairKit",
        "-Dversion=1.21.4",
        "-Dpackaging=jar"
    )}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")

    relocate("net.kyori", "net.kyori")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }

    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))

    implementation("net.kyori:adventure-api:4.24.0")
    implementation("net.kyori:adventure-text-minimessage:4.24.0")
}

tasks.named("build"){
    dependsOn(tasks.named("shadowJar"))
    if (copyBoolean)
        finalizedBy("copy")

    if (updateBoolean)
        finalizedBy("updateRepo")
}