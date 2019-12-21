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
    maven(url = "http://repo.spring.io/libs-release/") // Fuel
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.11")

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")

    compile("com.github.kittinunf.fuel", "fuel", "2.0.1")
    compile("org.slf4j", "slf4j-simple", "1.7.29")
    compile("org.apache.commons", "commons-text", "1.8")
    compile("net.zomis", "duga-core", "0.4")
    compile("com.amazonaws", "aws-lambda-java-core", "1.2.0")
    compile("com.amazonaws", "aws-java-sdk-dynamodb", "1.11.675")
    compile("com.amazonaws", "amazon-sqs-java-messaging-lib", "1.0.4")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}