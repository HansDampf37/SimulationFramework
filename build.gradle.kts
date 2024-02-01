import java.util.regex.Pattern.compile

plugins {
    kotlin("jvm") version "1.9.22"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
    implementation("com.formdev:flatlaf:3.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

application {
    // Define the main class for the application.
    mainClass = "Launcher"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = project.application.mainClass
    }
}

