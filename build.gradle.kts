import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("net.fabricmc.fabric-loom")
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "2.3.21"
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.

    maven("https://maven.nucleoid.xyz")

    exclusiveContent {
        forRepository {
            maven ("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("minions") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }

    for (settings in runs) {
        settings.vmArg("-XX:+AllowEnhancedClassRedefinition")
    }

    accessWidenerPath = file("src/main/resources/minions.classtweaker")
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")

    implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
    implementation("net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")

    val polymer_version = providers.gradleProperty("polymer_version").get()
    implementation("eu.pb4:polymer-core:${polymer_version}")
    implementation("eu.pb4:polymer-virtual-entity:${polymer_version}")
    implementation("eu.pb4:polymer-resource-pack:${polymer_version}")
    localRuntime("eu.pb4:polymer-autohost:${polymer_version}")
    implementation("eu.pb4:sgui:${providers.gradleProperty("sgui_version").get()}")
    implementation("xyz.nucleoid:server-translations-api:${providers.gradleProperty("server_translations_version").get()}")

    implementation("com.electronwill.night-config:toml:${providers.gradleProperty("night_config_version").get()}")

    compileOnly("maven.modrinth:universal-graves:${providers.gradleProperty("universal_graves_version").get()}")
}

tasks.processResources {
    val version = version
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    val projectName = project.name
    inputs.property("projectName", projectName)

    from("LICENSE") {
        rename { "${it}_$projectName" }
    }

    from("src/main/resources/assets/minions/lang") {
        into("data/minions/lang")
    }
}

// configure the maven publication
publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
        maven("https://maven.foxgalaxy.de/private") {
            name = "foxgalaxy"
            credentials(PasswordCredentials::class.java)
        }
    }
}