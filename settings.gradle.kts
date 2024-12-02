pluginManagement {
    val kotlinVersion = extra["kotlin.version"].toString()
    val kotlinterVersion = extra["kotlinter.version"].toString()
    val springBootVersion = extra["spring.boot.version"].toString()
    val graalvmNativeVersion = extra["graalvm.native.version"].toString()

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("org.jmailen.kotlinter") version kotlinterVersion
        id("org.springframework.boot") version springBootVersion
        id("org.graalvm.buildtools.native") version graalvmNativeVersion
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
include(":spring-application")
