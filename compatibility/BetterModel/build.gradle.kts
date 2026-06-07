subprojects {
    apply(plugin="java")
    repositories {
        mavenCentral()
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    configure<JavaPluginExtension> {
        disableAutoTargetJvm()
    }

    val targetJavaVersion = 21

    dependencies {
        addProvider("compileOnly", rootProject.libs.spigot.api)
        addProvider("compileOnly", rootProject.libs.citizens.main, Action {
            exclude(group="net.byteflux")
        })
        addProvider("compileOnly", rootProject.libs.annotations)
        add("compileOnly", project(":compatibility:shared"))
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
