import org.jmailen.gradle.kotlinter.tasks.InstallPreCommitHookTask

plugins {
    kotlin("jvm")
    id("org.jmailen.kotlinter")
}

repositories {
    mavenCentral()
}

allprojects {
    group = "io.github.tronto20"
}


tasks.register("installKotlinterPreCommitHook", InstallPreCommitHookTask::class.java) {
    this.group = "build setup"
    this.description = "Installs Kotlinter Git pre-commit hook"
}

tasks.getByName("prepareKotlinBuildScriptModel") {
    dependsOn(tasks.getByName("installKotlinterPrePushHook"))
    dependsOn(tasks.getByName("installKotlinterPreCommitHook"))
}
