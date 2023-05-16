package vgw.wallet

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val transactionType: TransactionType,
    val coins: Int,
    val transactionId: String,
    val transactionVersion: Int,
)

@Serializable
data class Balance(var transactionId: String, var version: Int, var coins: Int)

enum class TransactionType {
    Credit,
    Debit
}

// Off load this to a database when task up to task
val users = mutableListOf<User>()

@Serializable
data class User(val id: String, var balance: Balance, var transactions: MutableList<Transaction>) {

    fun debitWallet(coins: Int, transactionId: String): WalletResponse {
        if (balance.coins < coins) {
            return WalletResponse.InputError
        }
        val anyBefore: Boolean = !transactions.any { it.transactionType == TransactionType.Debit }

        return if (anyBefore) {
            balance.coins -= coins
            balance.transactionId = transactionId
            balance.version += 1

            val newTransaction = Transaction(TransactionType.Debit, coins, transactionId, balance.version)
            transactions.add(newTransaction)

            WalletResponse.Created
        } else {
            WalletResponse.DuplicateTransaction
        }
    }

    fun doesWalletExist(): WalletResponse {
        val user: User? = users.find { it.id == id }
        return if (user == null) {
            WalletResponse.NotFound
        } else {
            WalletResponse.Ok
        }
    }
}

fun creditWallet(walletID: String, coins: Int, transactionId: String): BalanceWalletResponse {
    val status = BalanceWalletResponse(WalletResponse.NotFound, Balance("NA", 0, 0))

    val wallet: User? = users.find { it.id == walletID }

    if (wallet == null) {
        status.response = WalletResponse.NotFound
        status.balance = Balance("NA", 0, 0)
    } else {
        val isDuplicateId: Boolean = wallet.transactions.any { it.transactionId == transactionId }

        if (isDuplicateId) {
            status.response = WalletResponse.DuplicateTransaction
            status.balance = wallet.balance
        } else {
            wallet.balance.coins += coins
            wallet.balance.transactionId = transactionId
            wallet.balance.version += 1

            val newTransaction = Transaction(TransactionType.Credit, coins, transactionId, wallet.balance.version)
            wallet.transactions.add(newTransaction)

            status.response = WalletResponse.Created
        }
    }

    return status
}


fun createNewUser(id: String): Balance {
    val balance = Balance("NA", 0, 0)
    val newUser = User(id, balance, mutableListOf())
    users.add(newUser)
    return balance
}

fun getUserBalance(id: String): Balance {
    val user = users.find { it.id == id }
    return user?.balance ?: createNewUser(id)
}