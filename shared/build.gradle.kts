plugins {
    java
}

val targetJavaVersion = 17
repositories {
    mavenCentral()
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.glaremasters.me/repository/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly("net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.0.0")
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
}