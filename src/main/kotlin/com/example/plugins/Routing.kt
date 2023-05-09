package com.example.plugins

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class User(val id: String, val balance: Balance)

data class Balance(val transactionId: String, val version: Int, val coins: Int)

val users = mutableListOf<User>()
var userCount = users.size

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
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
                    call.respond(user.balance)
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
    val balance = Balance("0", 1, 0)
    val newUser = User(id, balance)
    users.add(newUser)
    return balance
}
