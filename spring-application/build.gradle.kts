import dev.tronto.titiler.buildsrc.tasks.PathExec
import org.springframework.boot.buildpack.platform.build.PullPolicy

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.serialization")
    id("org.springframework.boot")
    id("org.graalvm.buildtools.native") apply false
    id("org.jmailen.kotlinter")
}


if (properties["image.native.enabled"].toString().toBoolean()) {
    apply(plugin = "org.graalvm.buildtools.native")
    afterEvaluate {
        tasks.bootBuildImage {
            val customEnvs = mutableMapOf<String, String>()
            (properties["image.native.compression"] as? String)?.let { compression ->
                customEnvs["BP_BINARY_COMPRESSION_METHOD"] = compression
            }

            val taskEnv = (this.environment.orNull ?: emptyMap())
            this.environment.set(taskEnv + customEnvs)
        }
        tasks.withType<Exec>().findByName("buildRunnerImage")?.apply {
            this.args("--build-arg")
            this.args("STACK_ID=io.buildpacks.stacks.jammy.tiny")
        }
    }

}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(projects.dependencies))
    implementation("io.github.oshai:kotlin-logging-jvm")
    implementation("org.slf4j:slf4j-api")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.locationtech.jts:jts-core")
    implementation("org.locationtech.jts.io:jts-io-common")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(kotlin("reflect"))
    implementation(projects.core)
}

val buildRunnerImageTask = tasks.register("buildRunnerImage", PathExec::class.java) {
    this.group = "build"
    workingDir = project.projectDir
    executable = "docker"
    setArgs(
        listOf(
            "build",
            ".",
            "-t",
            "docker.io/library/spring-application-runner:latest",
        )
    )

    val gdalVersion = properties["gdal.version"]
    args("--build-arg")
    args("GDAL_VERSION=${gdalVersion}")
}

tasks.bootBuildImage {
    dependsOn(buildRunnerImageTask)
    this.pullPolicy.set(PullPolicy.IF_NOT_PRESENT)
    this.runImage.set("docker.io/library/spring-application-runner:latest")
}

kotlin {
    jvmToolchain((properties["jvm.version"] as? String)?.toIntOrNull() ?: 21)
}

tasks.register("buildImage") {
    group = "build"
    dependsOn(tasks.bootBuildImage)
}
