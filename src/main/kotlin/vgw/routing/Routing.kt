package vgw.routing

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import vgw.wallet.*
import vgw.wallet.payloads.CreditPayload

fun Application.configureRouting() {
    routing {
        post("/wallets/{id}/credit") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val user: User = users.find { it.id == id }!!
                val jsonPayload = call.receive<String>()
                val payload = Json.decodeFromString<CreditPayload>(jsonPayload)

                val result = user.creditWallet(payload.coins, payload.transactionId)
                val httpStatus = ConvertWalletResponseToHTTPStatus(result)
                call.respond(httpStatus, user.balance)
            }
        }

        post("/wallets/{id}/debit") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val user: User = users.find { it.id == id }!!
                val jsonPayload = call.receive<String>()
                val payload = Json.decodeFromString<CreditPayload>(jsonPayload)

                val result = user.debitWallet(payload.coins, payload.transactionId)
                val httpStatus = ConvertWalletResponseToHTTPStatus(result)
                call.respond(httpStatus, user.balance)
            }
        }

        get("/wallets/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val bal = getUserBalance(id)
                if (bal.transactionId == "NA") {
                    call.respond(HttpStatusCode.NotFound)
                } else if (bal.transactionId != "NA") {
                    call.respond(HttpStatusCode.OK, bal)
                }
            }
        }
    }
}