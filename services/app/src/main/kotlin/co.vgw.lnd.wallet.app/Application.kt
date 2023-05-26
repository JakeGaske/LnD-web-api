package co.vgw.lnd.wallet.app

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import co.vgw.lnd.wallet.http.configureRouting
import kotlinx.serialization.json.Json

fun main() {

    val server = embeddedServer(
        Jetty,
        port = 8080,
        module = {
            install(ContentNegotiation) {
                json(Json)
            }
            configureRouting()
        }
    )
    server.start()
}