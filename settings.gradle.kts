pluginManagement {
    val kotlinVersion = extra["versions.kotlin"].toString()
    val kotlinterVersion = extra["versions.kotlinter"].toString()

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("org.jmailen.kotlinter") version kotlinterVersion
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "kotlin-titler"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":dependencies")
include(":core")
