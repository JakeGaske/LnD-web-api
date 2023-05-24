plugins {
    standardKotlinJvmModule()
    application
}

application {
    mainClass.set("co.vgw.lnd.wallet.app.ApplicationKt")
}

dependencies {
    implementation(project(":http"))
    implementation(libs.bundles.ktor.server.app)
    implementation(libs.slf4j)
}

tasks.distTar {
    archiveFileName.set("app-bundle.${archiveExtension.get()}")
}

repositories {
    mavenCentral()
}