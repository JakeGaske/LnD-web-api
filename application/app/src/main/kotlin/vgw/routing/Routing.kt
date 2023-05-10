package vgw.routing

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import vgw.user.*
import vgw.wallet.payloads.*

fun Application.configureRouting() {
    routing {
        post("/wallets/{id}/credit") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val user : User = users.find { it.id == id }!!
                val jsonPayload = call.receive<String>()
                val payload = Json.decodeFromString<CreditPayload>(jsonPayload)
                call.respond(user.creditWallet(payload.coins, payload.transactionId), user.balance)
            }
        }

        post("/wallets/{id}/debit") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val user : User = users.find { it.id == id }!!
                val jsonPayload = call.receive<String>()
                val payload = Json.decodeFromString<CreditPayload>(jsonPayload)

                if(user.balance.coins < payload.coins){
                    call.respond(HttpStatusCode.BadRequest, "Coins is smaller than payload")
                } else {
                    call.respond(user.debitWallet(payload.coins, payload.transactionId, 1 ), user.balance)
                }
            }
        }

        get("/wallets/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val user = users.find { it.id == id }
                if (user == null) {
                    createNewUser(id)
                    call.respond(HttpStatusCode.NotFound, "New user created")
                } else {
                    call.respond(HttpStatusCode.OK, user.balance)
                }
            }
        }

        get("/Users/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val user = users.find { it.id == id }
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(HttpStatusCode.OK, user.balance)
                }
            }
        }
    }
}