import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm")
    id("org.jmailen.kotlinter")
    id("com.vanniktech.maven.publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    api(kotlin("reflect"))
    api(projects.core)
    api(projects.springBootKitilerAutoconfigure)
    api("org.thymeleaf:thymeleaf")
    api("org.springframework:spring-webflux")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core")
}

val jvmVersion = (properties["jvm.version"] as? String)?.toIntOrNull() ?: 21
kotlin {
    jvmToolchain(jvmVersion)
}


mavenPublishing {
    beforeEvaluate {
        @Suppress("UnstableApiUsage")
        pomFromGradleProperties()
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
