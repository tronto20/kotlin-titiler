import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-platform`
    id("com.vanniktech.maven.publish")
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
        api("com.epages:restdocs-api-spec:${properties["spring.restdocs-api-spec.version"]}")
        api("com.epages:restdocs-api-spec-webtestclient:${properties["spring.restdocs-api-spec.version"]}")
        api("io.kotest.extensions:kotest-extensions-spring:1.3.0")
        api("com.ninja-squad:springmockk:4.0.2")
        api("io.swagger.parser.v3:swagger-parser:2.1.18")
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(project.group.toString(), "${rootProject.name}-${project.name}", project.version.toString())
    pom {
        inceptionYear.set("2024")
        this.name.set("kotlin-titiler-dependencies")
        this.description.set("Dependencies for kotlin-titiler.")
        this.url.set("http://github.com/tronto20/kotlin-titiler")
        licenses {
            license {
                this.name.set("MIT License")
                this.url.set("http://opensource.org/license/mit")
            }
        }
        developers {
            developer {
                this.id.set("tronto20")
                this.name.set("HyeongJun Shin")
                this.email.set("tronto980@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:git@github.com:tronto20/kotlin-titiler.git")
            developerConnection.set("scm:git:ssh://github.com/tronto20/kotlin-titiler.git")
            url.set("http://github.com/tronto20/kotlin-titiler/tree/main")
        }
    }
}
