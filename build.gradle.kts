
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.8"
    kotlin("plugin.serialization") version "1.9.23"
}

group = "com.cephonodes.yuki"
version = "0.0.1"

application {
    mainClass.set("com.cephonodes.yuki.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation ("io.ktor:ktor-serialization:$ktor_version")
//    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
//    implementation("org.jetbrains.exposed:exposed-crypt:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
//    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-json:$exposed_version")
//    implementation("org.jetbrains.exposed:exposed-money:$exposed_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("io.mockk:mockk:1.13.7")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

ktor {
    fatJar {
        archiveFileName.set("school-management-api.jar")
    }
}