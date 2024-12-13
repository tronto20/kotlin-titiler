plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jmailen.kotlinter")
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation(kotlin("reflect"))
    implementation("io.github.oshai:kotlin-logging-jvm")
    implementation("org.slf4j:slf4j-api")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.locationtech.jts:jts-core")
    implementation("org.gdal:gdal")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.springframework:spring-core")
    implementation("org.springframework:spring-expression")

    implementation("org.jetbrains.kotlinx:multik-core")
    implementation("org.jetbrains.kotlinx:multik-default")

    implementation("org.thymeleaf:thymeleaf")
}

kotlin {
    jvmToolchain((properties["jvm.version"] as? String)?.toIntOrNull() ?: 21)
}

publishing {
    this.publications {
        create<MavenPublication>("release") {
            from(components["java"])

            pom {
                inceptionYear.set("2024")
                this.name.set("titiler-core")
                this.description.set("Core of titiler.")
                this.url.set("http://github.com/tronto20/kotlin-titiler")

                licenses {
                    license {
                        this.name.set("MIT License")
                        this.url.set("http://www.opensource.org/licenses/mit-license.php")
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
    }

    this.repositories {
        this.mavenCentral {
            this.credentials {
                this.username = properties["publish.mavenCentral.username"] as String?
                this.username = properties["publish.mavenCentral.password"] as String?
            }
        }
    }
}


signing {
    useGpgCmd()
    val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")
    this.isRequired = isReleaseVersion && gradle.taskGraph.hasTask("publish")
    sign(publishing.publications["release"])
}
