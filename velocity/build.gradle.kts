import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("java")
    id("com.gradleup.shadow")
    id("com.modrinth.minotaur")
    id("io.papermc.hangar-publish-plugin")
}

group = rootProject.group
version = rootProject.version

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation(project(":api"))
    implementation("org.bstats:bstats-velocity:3.1.0")
    implementation("net.thenextlvl.core:files:2.0.1")
    implementation("net.thenextlvl.core:i18n:1.0.20")
    implementation("net.thenextlvl.core:version-checker:2.0.1") {
        exclude("com.google.code.gson")
    }

    annotationProcessor("org.projectlombok:lombok:1.18.36")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}


tasks.shadowJar {
    archiveBaseName.set("commander-velocity")
    relocate("org.bstats", "net.thenextlvl.commander.bstats")
    minimize()
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("velocity") {
        id.set("CommandControl")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.VELOCITY) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set((property("velocityVersions") as String)
                .split(",")
                .map { it.trim() })
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("USLuwMUi")
    versionType = if (isRelease) "release" else "beta"
    uploadFile.set(tasks.shadowJar)
    gameVersions.set((property("gameVersions") as String)
        .split(",")
        .map { it.trim() })
    loaders.add("velocity")
}