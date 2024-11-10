plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.netflix.dgs.codegen") version "6.3.0"
}

group = "civic-tech-pioneer"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:9.1.3"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

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

tasks.generateJava {
    typeMapping = mutableMapOf(
        "UUID" to "java.util.UUID",
    )
}