package vgw.wallet

import kotlinx.serialization.Serializable
import vgw.Transaction
import vgw.TransactionType
import vgw.UUIDSerializer
import vgw.transactions
import java.util.UUID

@Serializable
data class Balance(var transactionId: String, var version: Int, var coins: Int)

@Serializable
data class Wallet(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    var balance: Balance,
)

fun creditWallet(walletId: UUID, coins: Int, transactionId: String): QueryResponse {
    val wallet = getWallet(walletId)

    if (wallet == null) {
        val newWallet = Wallet(walletId, Balance(transactionId, 1, coins))
        addNewTransaction(walletId, TransactionType.Credit, coins, transactionId, newWallet.balance.version)
        return QueryResponse.Success(newWallet)
    } else {
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

fun debitWallet(walletId: UUID, coins: Int, transactionId: String): QueryResponse {
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
    walletId: UUID,
    transactionType: TransactionType,
    coins: Int,
    transactionId: String,
    version: Int,
) {
    val newTransaction = Transaction(walletId, transactionType, coins, transactionId, version)
    transactions.add(newTransaction)
}

fun doesWalletExist(walletId: UUID): QueryResponse {
    val wallet = getWallet(walletId)

    return if (wallet == null) {
        QueryResponse.WalletNotFound()
    } else {
        QueryResponse.Success(wallet)
    }
}

private fun getWallet(walletId: UUID): Wallet? {
    val transactionCheck = transactions.find { it.walletId == walletId }
    return if (transactionCheck == null) {
        null
    } else {
        Wallet(
            walletId,
            Balance(getLatestTransactionId(walletId), getLastVersion(walletId), getWalletAmount(walletId))
        )
    }
}

private fun getLatestTransactionId(walletId: UUID): String {
    val highestVersionTransaction =
        transactions.filter { it.walletId == walletId }.maxByOrNull { it.version }
    return highestVersionTransaction?.id ?: "NA"
}

private fun getWalletAmount(walletID: UUID): Int {
    var amount = 0

    for (transaction in transactions.filter { it.walletId == walletID }) {
        when (transaction.type) {
            TransactionType.Credit -> {
                amount += transaction.amount
            }

            TransactionType.Debit -> {
                amount -= transaction.amount
            }
        }
    }

    return amount
}

private fun getLastVersion(walletId: UUID): Int {
    val highestVersionTransaction =
        transactions.filter { it.walletId == walletId }.maxByOrNull { it.version }
    return highestVersionTransaction?.version ?: 0
}