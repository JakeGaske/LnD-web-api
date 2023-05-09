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
data class User(val id: String, var balance: Balance){
    fun creditWallet(coins: Int, transactionId: String, version: Int) {
        balance.coins += coins
        balance.transactionId = transactionId
        balance.version = version
    }

    fun setupUserWallet() {
        balance.coins = 1000;
        balance.transactionId = "tx123"
        balance.version = 1;
    }
}
@Serializable
data class Balance(var transactionId: String, var version: Int, var coins: Int)

val users = mutableListOf<User>()
var userCount = users.size

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/wallets/{id}/credit") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            } else {
                val user : User = users.find { it.id == id }!!
                if(user.balance.transactionId == "tx123"){
                    call.respond(HttpStatusCode.Created)
                } else {
                    // RETURN JSON OF USER BALANCE
                    call.respond(HttpStatusCode.OK, user.balance)
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

        get("/users/count") {
            userCount = users.size
            call.respondText(userCount.toString())
        }
    }
}

fun createNewUser(id: String): Balance {
    // Here you can implement your logic to create a new user with a balance
    // For the sake of simplicity, let's just create a new user with a hardcoded balance for now
    val balance = Balance("NA", 0, 0)
    val newUser = User(id, balance)
    newUser.setupUserWallet()
    users.add(newUser)
    return balance
}

data class CreditPayload(
    val transactionId: String,
    val version: Int,
    val coins: Int
)


fun creditUserWallet(jsonPayload: String, userId: String) {
    val payload = Json.decodeFromString<CreditPayload>(jsonPayload)

    // find the user with the specified ID or create a new user if none exists
    val user: User = (users.find { it.id == userId } ?: createNewUser(userId)) as User

    // credit the user's wallet with the specified coins
    user.creditWallet(payload.coins, payload.transactionId, payload.version)
}