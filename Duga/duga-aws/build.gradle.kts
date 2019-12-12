import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow").version("5.2.0")
}

group = "net.zomis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "http://www.zomis.net/maven/")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile("org.slf4j", "slf4j-simple", "1.7.29")
    compile("net.zomis", "duga-core", "0.4")
    compile("com.amazonaws", "aws-lambda-java-core", "1.2.0")
    compile("com.amazonaws", "aws-java-sdk-dynamodb", "1.11.675")
    compile("com.amazonaws", "amazon-sqs-java-messaging-lib", "1.0.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}