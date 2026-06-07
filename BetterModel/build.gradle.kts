subprojects {
    apply(plugin="java")
    repositories {
        mavenCentral()
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://repo.glaremasters.me/repository/public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    configure<JavaPluginExtension> {
        disableAutoTargetJvm()
    }

    val targetJavaVersion = 21

    dependencies {
        add("compileOnly", "org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
        add("compileOnly", "net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT")
        add("compileOnly", "org.jetbrains:annotations:24.0.0")

        add("compileOnly", project(":shared"))
    }
    tasks {
        withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
                options.release.set(targetJavaVersion)
            }
        }
    }
}
