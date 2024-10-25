plugins {
    kotlin("jvm") version "2.0.21"
}

group = "civic-tech-pioneer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")

    val koTestVersion = "5.9.1"
    testImplementation("io.kotest:kotest-runner-junit5:$koTestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$koTestVersion")
    testImplementation("io.kotest:kotest-property:$koTestVersion")
    testImplementation("io.kotest.extensions:kotest-property-arbs:2.1.2")
}

tasks.test {
    useJUnitPlatform()
}