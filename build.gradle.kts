import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.5.10"
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

    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }
}

dependencies {
    paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    implementation("com.github.luben:zstd-jni:1.5.0-4")
    implementation("net.kyori:adventure-api:4.13.1")
    implementation("redis.clients:jedis:4.3.1")
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("com.palmergames.bukkit.towny:towny:0.99.0.0")
    compileOnly("pl.betoncraft:betonquest:1.12.10")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.8-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.2.6")
}

group = "net.laboulangerie"
version = "2.3.1"
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
            artifact(tasks.named("reobfJar"))
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
