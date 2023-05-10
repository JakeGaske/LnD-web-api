package vgw.user

import io.ktor.http.*
import kotlinx.serialization.Serializable
import vgw.wallet.*

// Off load this to a database when task up to task
val users = mutableListOf<User>()

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

fun createNewUser(id: String): Balance {
    val balance = Balance("NA", 0, 0)
    val newUser = User(id, balance, mutableListOf())
    users.add(newUser)
    return balance
}