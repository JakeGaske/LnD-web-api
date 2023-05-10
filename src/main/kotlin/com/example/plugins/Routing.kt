package com.example.plugins

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class User(val id: String, var balance: Balance, var transactions: MutableList<Transaction>){
    fun creditWallet(coins: Int, transactionId: String) : HttpStatusCode {
        val anyBefore: Boolean = !transactions.any { it.transactionId == transactionId }
        if(anyBefore){
            balance.coins += coins
            balance.transactionId = transactionId
            balance.version += 1

            val newTransaction = Transaction(TransactionType.Credit, coins, transactionId, balance.version)
            transactions.add(newTransaction)

            return HttpStatusCode.Created
        } else {
            return HttpStatusCode.Accepted
        }
    }

    fun debitWallet(coins: Int, transactionId: String, version: Int) : HttpStatusCode {
        val anyBefore: Boolean = !transactions.any { it.transactionType == TransactionType.Debit }

        if(anyBefore){
            balance.coins -= coins
            balance.transactionId = transactionId
            balance.version += version

            val newTransaction = Transaction(TransactionType.Debit, coins, transactionId, balance.version)
            transactions.add(newTransaction)

            return HttpStatusCode.Created
        } else {
            return HttpStatusCode.Accepted
        }
    }
}

enum class TransactionType{
    Credit,
    Debit
}
@Serializable
data class Transaction(val transactionType: TransactionType, val coins: Int, val transactionId: String, val transactionVersion: Int)
@Serializable
data class Balance(var transactionId: String, var version: Int, var coins: Int)

val users = mutableListOf<User>()
var userCount = users.size

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

        get("/users/count") {
            userCount = users.size
            call.respondText(userCount.toString())
        }
    }
}

fun createNewUser(id: String): Balance {
    val balance = Balance("NA", 0, 0)
    val newUser = User(id, balance, mutableListOf())
    users.add(newUser)
    return balance
}

@Serializable
data class CreditPayload(
    val transactionId: String,
    val coins: Int
)