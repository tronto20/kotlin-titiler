import org.jmailen.gradle.kotlinter.tasks.InstallPreCommitHookTask

plugins {
    kotlin("jvm")
    id("org.jmailen.kotlinter")
}

repositories {
    mavenCentral()
}

allprojects {
    group = "dev.tronto"
}

tasks.register("installKotlinterPreCommitHook", InstallPreCommitHookTask::class.java) {
    this.group = "build setup"
    this.description = "Installs Kotlinter Git pre-commit hook"
}

tasks.getByName("prepareKotlinBuildScriptModel") {
    dependsOn(tasks.getByName("installKotlinterPrePushHook"))
    dependsOn(tasks.getByName("installKotlinterPreCommitHook"))
}

kotlin {
    jvmToolchain((properties["jvm.version"] as? String)?.toIntOrNull() ?: 21)
}
