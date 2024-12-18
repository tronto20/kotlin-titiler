import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.jmailen.kotlinter")
    id("com.vanniktech.maven.publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation(kotlin("reflect"))
    compileOnly(projects.core)
    compileOnly("org.thymeleaf:thymeleaf")
    compileOnly("org.springframework:spring-webflux")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
}

kotlin {
    jvmToolchain((properties["jvm.version"] as? String)?.toIntOrNull() ?: 21)
}


mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(project.group.toString(), project.name, project.version.toString())
    pom {
        inceptionYear.set("2024")
        this.name.set("spring-boot-titiler-autoconfigure")
        this.description.set("Spring Boot autoconfigure for kotlin-titiler.")
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
