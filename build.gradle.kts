plugins {
    java
    `maven-publish`
    id ("com.gradleup.shadow") version "9.3.0"
    id ("com.github.gmazzo.buildconfig") version "5.6.7"
}

buildscript {
    repositories.mavenCentral()
    dependencies.classpath("top.mrxiaom:LibrariesResolver-Gradle:1.7.17")
}

group = "top.mrxiaom.citizensmodels"
version = "1.0.1"
val base = top.mrxiaom.gradle.LibraryHelper(project)
val targetJavaVersion = 17
val pluginBaseModules = base.modules.run { listOf(library, l10n) }
val shadowGroup = "top.mrxiaom.citizensmodels.libs"

repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.helpch.at/releases/")
    maven("https://jitpack.io")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.glaremasters.me/repository/public/")
    maven("https://repo.rosewooddev.io/repository/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly(base.depend.annotations)

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT")

    base.library("net.kyori:adventure-api:4.22.0")
    base.library("net.kyori:adventure-text-minimessage:4.22.0")
    base.library("net.kyori:adventure-text-serializer-gson:4.22.0")
    base.library("net.kyori:adventure-text-serializer-plain:4.22.0")

    for (artifact in pluginBaseModules) {
        implementation(artifact)
    }
    implementation(base.resolver.lite)

    for (subproject in project.project(":ModelEngine").subprojects) {
        implementation(subproject)
    }
}
buildConfig {
    className("BuildConstants")
    packageName("top.mrxiaom.citizensmodels")

    base.doResolveLibraries()

    buildConfigField("String", "VERSION", "\"${project.version}\"")
    buildConfigField("java.time.Instant", "BUILD_TIME", "java.time.Instant.ofEpochSecond(${System.currentTimeMillis() / 1000L}L)")
    buildConfigField("String[]", "RESOLVED_LIBRARIES", base.join())
}

top.mrxiaom.gradle.LibraryHelper.initJava(project, base, targetJavaVersion, true)
top.mrxiaom.gradle.LibraryHelper.initPublishing(project)

tasks {
    shadowJar {
        configurations.add(project.configurations.runtimeClasspath.get())
        mapOf(
            "top.mrxiaom.pluginbase" to "base",
        ).forEach { (original, target) ->
            relocate(original, "$shadowGroup.$target")
        }
    }
}
