plugins {
    java
}

val targetJavaVersion = 17
repositories {
    mavenCentral()
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}
dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.citizens.main) { exclude(group="net.byteflux") }
    compileOnly(libs.annotations)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
}
