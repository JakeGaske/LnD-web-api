package vgw.wallet

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(val transactionType: TransactionType, val coins: Int, val transactionId: String, val transactionVersion: Int)
@Serializable
data class Balance(var transactionId: String, var version: Int, var coins: Int)

enum class TransactionType{
    Credit,
    Debit
}

// Off load this to a database when task up to task
val users = mutableListOf<User>()

@Serializable
data class User(val id: String, var balance: Balance, var transactions: MutableList<Transaction>){
    fun creditWallet(coins: Int, transactionId: String) : WalletResponse {
        val anyBefore: Boolean = !transactions.any { it.transactionId == transactionId }
        if(anyBefore){
            balance.coins += coins
            balance.transactionId = transactionId
            balance.version += 1

            val newTransaction = Transaction(TransactionType.Credit, coins, transactionId, balance.version)
            transactions.add(newTransaction)

            return WalletResponse.Created
        } else {
            return WalletResponse.DuplicateTransaction
        }
    }

    fun debitWallet(coins: Int, transactionId: String) : WalletResponse {
        if(balance.coins < coins){
            return WalletResponse.InputError
        }
        val anyBefore: Boolean = !transactions.any { it.transactionType == TransactionType.Debit }

        if(anyBefore){
            balance.coins -= coins
            balance.transactionId = transactionId
            balance.version += 1

            val newTransaction = Transaction(TransactionType.Debit, coins, transactionId, balance.version)
            transactions.add(newTransaction)

            return WalletResponse.Created
        } else {
            return WalletResponse.DuplicateTransaction
        }
    }
}

fun createNewUser(id: String): Balance {
    val balance = Balance("NA", 0, 0)
    val newUser = User(id, balance, mutableListOf())
    users.add(newUser)
    return balance
}

fun getUserBalance(id: String) : Balance{
    val user = users.find { it.id == id }
    if (user == null) {
        return createNewUser(id)
    } else {
        return user.balance
    }
}