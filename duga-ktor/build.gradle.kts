import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project

plugins {
    application
    kotlin("jvm") version "2.3.10"
    id("com.gradleup.shadow") version "9.3.1"
    id("com.github.ben-manes.versions") version "0.39.0"
}

group = "net.zomis.duga"
version = "0.0.1-SNAPSHOT"

application {
    mainClass = "net.zomis.duga.DugaMainKt"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://www.zomis.net/maven") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("net.zomis:machlearn:0.1.0-SNAPSHOT")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.3")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-gson:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-encoding:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.github.shyiko.skedule:skedule:0.4.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.0")
    implementation("org.slf4j:slf4j-api:2.0.17")

    implementation("software.amazon.awssdk:dynamodb-enhanced:2.16.80")
    implementation("software.amazon.awssdk:apache-client:2.16.80")
    implementation("software.amazon.awssdk:sdk-core:2.16.80")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
