subprojects {
    apply(plugin="java")
    repositories {
        mavenCentral()
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://repo.glaremasters.me/repository/public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    val targetJavaVersion = 17

    dependencies {
        add("compileOnly", "org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
        add("compileOnly", "net.citizensnpcs:citizens-main:2.0.41-SNAPSHOT")
        add("compileOnly", "org.jetbrains:annotations:24.0.0")
        if (project.name != "shared") {
            add("compileOnly", project(":ModelEngine:shared"))
        }
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
