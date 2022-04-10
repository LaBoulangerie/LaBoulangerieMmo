import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.3.5"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenLocal()

    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://repo.glaremasters.me/repository/towny/")
    }

    maven {
        url = uri("https://betonquest.org/nexus/repository/betonquest/")
    }

    maven {
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    implementation("com.github.luben:zstd-jni:1.5.0-4")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.palmergames.bukkit.towny:towny:0.98.0.0")
    compileOnly("pl.betoncraft:betonquest:1.12.8")
}

group = "net.laboulangerie"
version = "1.2.0"
description = "LaBoulangerieMmo"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks {
  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
  processResources {
    filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
  reobfJar {
    outputJar.set(layout.buildDirectory.file("libs/${project.name}.jar"))
  }
  shadowJar {
      fun reloc(pkg: String) = relocate(pkg, "net.laboulangerie.dependency.$pkg")
  }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
publishing {
    publications {
        create<MavenPublication>("default") {
            setArtifactId("laboulangeriemmo")
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/LaBoulangerie/LaBoulangerieMmo")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
