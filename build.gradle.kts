import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("com.gradleup.shadow") version "8.3.5"
}

repositories {
    mavenLocal()

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
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
        url = uri("https://repo.betonquest.org/betonquest/")
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

    maven {
        url = uri("https://mvn.lib.co.nz/public")
        content {
            includeGroup("me.libraryaddict.disguises")
        }
    }
}

configurations {
    "compileClasspath" {
        resolutionStrategy.force("com.google.guava:guava:33.2.1-jre")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.github.luben:zstd-jni:1.5.0-4")
    compileOnly("redis.clients:jedis:5.1.3") {
        isTransitive = false
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("fr.minelet:minelet-api:1.2.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.palmergames.bukkit.towny:towny:0.100.4.0")
    compileOnly("org.betonquest:betonquest:3.0.2") {
        exclude(group = "dev.faststats.metrics")
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.15") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.15") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.4.1") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldedit:worldedit-core:7.4.1") {
        isTransitive = false
    }
    compileOnly("net.kyori:adventure-text-serializer-ansi:4.26.1")
    compileOnly("org.apache.commons:commons-pool2:2.12.0")
    compileOnly("io.lumine:Mythic-Dist:5.4.1")
    compileOnly("me.libraryaddict.disguises:libsdisguises:11.0.0")
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("redis.clients:jedis:5.1.3")
    testImplementation("fr.minelet:minelet-api:1.2.0")
    testImplementation("net.kyori:adventure-text-serializer-ansi:4.26.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

group = "net.laboulangerie"
version = "2.3.2"
description = "LaBoulangerieMmo"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks {
  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }
  compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(21)
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
tasks.test {
    useJUnitPlatform()
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
