import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("com.google.cloud.tools.jib") version "3.3.1"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
}

group = "org.romancha"
version = "1.0-SNAPSHOT"

val ktor_version: String by project
val lets_plot_kotlin_version: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.inmo:tgbotapi:4.2.2")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("org.slf4j:slf4j-simple:2.0.5")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:$lets_plot_kotlin_version")
    implementation("org.jetbrains.lets-plot:lets-plot-image-export:2.5.1")

    implementation("org.mapdb:mapdb:3.0.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("org.romancha.autofon.MainKt")
}

jib {
    container {
        entrypoint = listOf("bash", "-c", "/entrypoint.sh")
    }
    extraDirectories {
        paths {
            path {
                setFrom(file("src/main/jib"))
                into = "/"
            }
        }
        permissions.set(mapOf("/entrypoint.sh" to "755"))
    }
}