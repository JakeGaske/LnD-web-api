plugins {
    standardKotlinJvmModule()
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
}