import dev.tronto.titiler.buildsrc.tasks.PathExec
import org.springframework.boot.buildpack.platform.build.PullPolicy
import org.springframework.boot.gradle.tasks.bundling.DockerSpec.DockerRegistrySpec

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

if (properties["image.debug.enabled"].toString().toBoolean()) {
    afterEvaluate {
        tasks.bootBuildImage {
            val envs = environment.get().toMutableMap()
            val javaOps = envs["BPE_APPEND_JAVA_TOOL_OPTIONS"]?.split(' ') ?: emptyList()

            val debugPort = properties["image.debug.port"]?.toString()?.toIntOrNull() ?: 5005
            val suspend = properties["image.debug.suspend"]?.toString()?.toBoolean() ?: false
            val suspendString = if (suspend) "y" else "n"

            envs["BPE_DELIM_JAVA_TOOL_OPTIONS"] = " "
            envs["BPE_APPEND_JAVA_TOOL_OPTIONS"] =
                (javaOps + "-agentlib:jdwp=transport=dt_socket,server=y,suspend=$suspendString,address=*:$debugPort")
                    .joinToString(separator = " ")
            environment.set(envs)
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
    implementation("org.thymeleaf:thymeleaf")

    // temp
    implementation("org.gdal:gdal")
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
    this.runImage.set("docker.io/library/spring-application-runner:latest")
    this.pullPolicy.set(PullPolicy.IF_NOT_PRESENT)

    fun DockerRegistrySpec.configure(name: String) {
        properties["image.registry.$name.url"]?.let { url.set(it as String) }
        properties["image.registry.$name.username"]?.let { username.set(it as String) }
        properties["image.registry.$name.password"]?.let { password.set(it as String) }
        properties["image.registry.$name.email"]?.let { email.set(it as String) }
        properties["image.registry.$name.token"]?.let { token.set(it as String) }
    }

    docker {
        publishRegistry.configure("publish")
        builderRegistry.configure("builder")
    }

    val baseName = (properties["image.name"] as? String?)?.trimEnd('/') ?: "docker.io/kotlin-titiler"
    val tags = (properties["image.tags"] as? String?)
        ?.split(',')
        ?.map { it.trim() }
        ?.ifEmpty { null }
        ?: emptyList()
    val versionTags = (properties["image.version-tags"] as? String?)
        ?.split(',')
        ?.map { "$version-${it.trim()}" }
        ?.ifEmpty { null }
        ?: emptyList()

    val defaultTags = if ((properties["image.default-tags"] as? String?).toBoolean()) {
        listOf(version.toString(), "latest")
    } else {
        emptyList()
    }

    this.tags.set((tags + versionTags + defaultTags).map { "$baseName:$it" })
    this.imageName.set(this.tags.get().firstOrNull() ?: "$baseName:$version")


    (properties["image.push"] as? String?)?.let { publish.set(it.toBoolean()) }
}

kotlin {
    jvmToolchain((properties["jvm.version"] as? String)?.toIntOrNull() ?: 21)
}

tasks.register("buildImage") {
    group = "build"
    dependsOn(tasks.bootBuildImage)
}
