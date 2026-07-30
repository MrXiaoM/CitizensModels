import top.mrxiaom.gradle.LibraryHelper

plugins {
    java
    `maven-publish`
    id ("com.gradleup.shadow") version "9.3.0"
    id ("com.github.gmazzo.buildconfig") version "5.6.7"
}

buildscript {
    repositories.mavenCentral()
    dependencies.classpath("top.mrxiaom:LibrariesResolver-Gradle:1.7.32")
}

group = "top.mrxiaom.citizensmodels"
version = "1.0.3"
val base = LibraryHelper(project)
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
    maven("https://repo.rosewooddev.io/repository/public/")
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.annotations)

    compileOnly(libs.papi)
    compileOnly(libs.citizens.main) { exclude(group="net.byteflux") }

    base.library(LibraryHelper.adventure("4.25.0"))
    base.collectPluginHolders()

    implementation("com.github.technicallycoded:FoliaLib:0.4.4") { isTransitive = false }
    for (artifact in pluginBaseModules) {
        implementation(artifact)
    }
    implementation(base.resolver.lite)

    implementation(project(":compatibility:shared"))
    for (subproject in project.project(":compatibility:ModelEngine").subprojects) {
        implementation(subproject)
    }
    for (subproject in project.project(":compatibility:BetterModel").subprojects) {
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

LibraryHelper.initJava(project, base, targetJavaVersion, true)
LibraryHelper.initPublishing(project)

tasks {
    shadowJar {
        configurations.add(project.configurations.runtimeClasspath.get())
        mapOf(
            "top.mrxiaom.pluginbase" to "base",
            "com.tcoded.folialib" to "folialib",
        ).forEach { (original, target) ->
            relocate(original, "$shadowGroup.$target")
        }
    }
}
