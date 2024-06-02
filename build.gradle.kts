plugins {
    kotlin("jvm")
    id("org.jmailen.kotlinter")
}

repositories {
    mavenCentral()
}

tasks.prepareKotlinBuildScriptModel {
    dependsOn(tasks.installKotlinterPrePushHook)
}


allprojects {
    group = "io.github.tronto20"
}
