package vgw.routing

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import vgw.wallet.QueryResponse
import vgw.wallet.creditWallet
import vgw.wallet.debitWallet
import vgw.wallet.doesWalletExist
import vgw.wallet.payloads.TransactionPayload

fun Application.configureRouting() {
    routing {
        post("/wallets/{id}/credit") {
            val walletId =
                call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No Wallet Id provided")
            val jsonPayload = call.receive<TransactionPayload>()

            when (val result = creditWallet(walletId, jsonPayload.coins, jsonPayload.transactionId)) {
                is QueryResponse.Success -> call.respond(HttpStatusCode.Created, result.wallet.balance)
                is QueryResponse.DuplicateTransaction -> call.respond(
                    HttpStatusCode.Accepted,
                    result.wallet.balance
                )

                else -> {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }

        post("/wallets/{id}/debit") {
            val walletId =
                call.parameters["id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "No Wallet Id provided")
            val jsonPayload = call.receive<TransactionPayload>()

            when (val result = debitWallet(walletId, jsonPayload.coins, jsonPayload.transactionId)) {
                is QueryResponse.Success -> call.respond(HttpStatusCode.Created, result.wallet.balance)
                is QueryResponse.DuplicateTransaction -> call.respond(
                    HttpStatusCode.Accepted,
                    result.wallet.balance
                )

                is QueryResponse.InsufficientFunds -> call.respond(HttpStatusCode.BadRequest, result.msg)
                else -> {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }

        get("/wallets/{id}") {
            val walletId =
                call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "No Wallet Id provided")

            when (val result = doesWalletExist(walletId)) {
                is QueryResponse.WalletNotFound -> call.respond(HttpStatusCode.NotFound)
                is QueryResponse.Success -> call.respond(HttpStatusCode.OK, result.wallet.balance)
                else -> {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}