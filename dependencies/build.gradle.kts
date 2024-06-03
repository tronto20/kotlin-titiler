plugins {
    `java-platform`
}

javaPlatform {
    this.allowDependencies()
}

dependencies {
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${properties["versions.kotlin.coroutines"]}"))
    api(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:${properties["versions.kotlin.serialization"]}"))
    api(platform("io.kotest:kotest-bom:5.8.1"))
    constraints {
        api("io.github.oshai:kotlin-logging-jvm:6.0.9")
        api("io.mockk:mockk:1.13.10")
        api("org.locationtech.jts:jts-core:1.19.0")
        api("org.locationtech.jts.io:jts-io-common:1.19.0")
        api("org.gdal:gdal:3.9.0")
    }
}
