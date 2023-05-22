package vgw.wallet

import kotlinx.serialization.Serializable
import vgw.Transaction
import vgw.TransactionType
import vgw.transactions

@Serializable
data class Balance(var transactionId: String, var version: Int, var coins: Int)

@Serializable
data class Wallet(val id: String, var balance: Balance)

fun creditWallet(walletId: String, coins: Int, transactionId: String): QueryResponse {
    val wallet = getWallet(walletId)

    if (wallet == null) {
        // Create the wallet and credit it here
        val newWallet = Wallet(walletId, Balance(transactionId, 1, coins))
        addNewTransaction(walletId, TransactionType.Credit, coins, transactionId, newWallet.balance.version)
        return QueryResponse.Success(newWallet)
    } else {
        // When a wallet already exists do this
        val isDuplicateTransaction: Boolean =
            transactions.any { it.id == transactionId && it.type == TransactionType.Credit && it.walletId == walletId }
        return if (isDuplicateTransaction) {
            QueryResponse.DuplicateTransaction(getWallet(walletId)!!)
        } else {
            addNewTransaction(
                walletId,
                TransactionType.Credit,
                coins,
                transactionId,
                getLastVersion(walletId) + 1
            )
            QueryResponse.Success(getWallet(walletId)!!)
        }
    }
}

fun debitWallet(walletId: String, coins: Int, transactionId: String): QueryResponse {
    val wallet = getWallet(walletId)!!

    return if (wallet.balance.coins < coins) {
        QueryResponse.InsufficientFunds()
    } else {
        val isDuplicateTransaction: Boolean =
            transactions.any { it.id == transactionId && it.type == TransactionType.Debit && it.walletId == walletId }

        if (isDuplicateTransaction) {
            QueryResponse.DuplicateTransaction(wallet)
        } else {
            addNewTransaction(
                walletId,
                TransactionType.Debit,
                coins,
                transactionId,
                getLastVersion(walletId) + 1
            )
            QueryResponse.Success(getWallet(walletId)!!)
        }
    }
}

private fun addNewTransaction(
    walletId: String,
    transactionType: TransactionType,
    coins: Int,
    transactionId: String,
    version: Int,
) {
    val newTransaction = Transaction(walletId, transactionType, coins, transactionId, version)
    transactions.add(newTransaction)
}

fun doesWalletExist(walletId: String): QueryResponse {
    val wallet = getWallet(walletId)

    return if (wallet == null) {
        QueryResponse.WalletNotFound()
    } else {
        QueryResponse.Success(wallet)
    }
}

private fun getWallet(walletId: String): Wallet? {
    val transactionCheck = transactions.find { it.walletId == walletId }
    return if (transactionCheck == null) {
        null
    } else {
        Wallet(walletId, Balance(getLatestTransactionId(walletId), getLastVersion(walletId), getWalletAmount(walletId)))
    }
}

private fun getLatestTransactionId(walletId: String): String {
    val highestVersionTransaction = transactions.filter { it.walletId == walletId }.maxByOrNull { it.version }
    return highestVersionTransaction?.id ?: "NA"
}

private fun getWalletAmount(walletID: String): Int {
    var amount = 0

    for (transaction in transactions.filter { it.walletId == walletID }) {
        when (transaction.type) {
            TransactionType.Credit -> {
                amount += transaction.coins
            }

            TransactionType.Debit -> {
                amount -= transaction.coins
            }
        }
    }

    return amount
}

private fun getLastVersion(walletId: String): Int {
    val highestVersionTransaction = transactions.filter { it.walletId == walletId }.maxByOrNull { it.version }
    return highestVersionTransaction?.version ?: 0
}