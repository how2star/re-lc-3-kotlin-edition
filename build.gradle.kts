plugins {
    kotlin("jvm") version "2.0.0"
    application
}

application {
    mainClass = "Main"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}