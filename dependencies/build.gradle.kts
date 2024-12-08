plugins {
    `java-platform`
}

javaPlatform {
    this.allowDependencies()
}

dependencies {
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${properties["kotlin.coroutines.version"]}"))
    api(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:${properties["kotlin.serialization.version"]}"))
    api(platform("org.springframework.boot:spring-boot-dependencies:${properties["spring.boot.version"]}"))

    api(platform("io.kotest:kotest-bom:5.8.1"))
    api(platform("org.slf4j:slf4j-bom:2.0.13"))
    constraints {
        api("io.github.oshai:kotlin-logging-jvm:6.0.9")
        api("io.mockk:mockk:1.13.10")
        api("org.locationtech.jts:jts-core:1.19.0")
        api("org.locationtech.jts.io:jts-io-common:1.19.0")
        api("org.gdal:gdal:${properties["gdal.version"]}")
        api("org.jetbrains.kotlinx:multik-core:0.2.3")
        api("org.jetbrains.kotlinx:multik-default:0.2.3")
    }
}
