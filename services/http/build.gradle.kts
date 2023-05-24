dependencies {
    implementation(project(":domain"))
    implementation(libs.bundles.ktor.server.app)
}

plugins {
    standardKotlinJvmModule()
}

repositories {
    mavenCentral()
}