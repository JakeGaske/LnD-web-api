package vgw.routing

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import vgw.wallet.*
import vgw.wallet.payloads.CreditPayload

fun Application.configureRouting() {
    routing {
        post("/wallets/{id}/credit") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val jsonPayload = call.receive<CreditPayload>()
                val result = creditWallet(id, jsonPayload.coins, jsonPayload.transactionId)

                when (result.response) {
                    WalletResponse.NotFound -> call.respond(HttpStatusCode.NotFound)
                    WalletResponse.DuplicateTransaction -> call.respond(HttpStatusCode.Accepted, result.balance)
                    WalletResponse.Created -> call.respond(HttpStatusCode.Created, result.balance)
                    WalletResponse.InputError -> call.respond(HttpStatusCode.BadRequest, result.balance)
                    else -> {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }

        post("/wallets/{id}/debit") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val jsonPayload = call.receive<CreditPayload>()
                val result = debitWallet(id, jsonPayload.coins, jsonPayload.transactionId)

                when (result.response) {
                    WalletResponse.NotFound -> call.respond(HttpStatusCode.NotFound)
                    WalletResponse.DuplicateTransaction -> call.respond(HttpStatusCode.Accepted, result.balance)
                    WalletResponse.Created -> call.respond(HttpStatusCode.Created, result.balance)
                    WalletResponse.InputError -> call.respond(HttpStatusCode.BadRequest, result.balance)
                    else -> {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }

        get("/wallets/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val result = getWalletBalance(id)

                when (result.response) {
                    WalletResponse.NotFound -> call.respond(HttpStatusCode.NotFound)
                    WalletResponse.Ok -> call.respond(HttpStatusCode.OK, result.balance)
                    else -> {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }
        }
    }
}