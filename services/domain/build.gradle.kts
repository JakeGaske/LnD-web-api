dependencies {
    implementation(libs.ktor.serialization.kotlinx.json)
    testImplementation(libs.kotlin.test.junit)
}


plugins {
    standardKotlinJvmModule()
}

repositories {
    mavenCentral()
}