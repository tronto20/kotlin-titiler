plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jmailen.kotlinter")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation("io.github.oshai:kotlin-logging-jvm")
    implementation("org.slf4j:slf4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.locationtech.jts:jts-core")
    implementation("org.locationtech.jts.io:jts-io-common")
    implementation("org.gdal:gdal")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
}

tasks.lintKotlin {
    dependsOn(tasks.formatKotlin)
}
