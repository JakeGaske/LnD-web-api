dependencies {
    implementation(project(":domain"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.jetty)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.contentNegotiation)
}

plugins {
    standardKotlinJvmModule()
}

repositories {
    mavenCentral()
}