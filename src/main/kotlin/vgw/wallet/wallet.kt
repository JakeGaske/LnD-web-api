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

class WalletManager {
    fun creditWallet(walletId: UUID, amount: Int, transactionId: String): QueryResponse {
        val wallet = getWallet(walletId)

        if (wallet == null) {
            val newWallet = Wallet(walletId, Balance(transactionId, 1, amount))
            addNewTransaction(
                walletId,
                TransactionType.Credit,
                amount,
                transactionId,
                newWallet.balance.version,
                amount
            )
            return QueryResponse.Success(newWallet)
        } else {
            val isDuplicateTransaction: Boolean =
                transactions.any { it.id == transactionId && it.type == TransactionType.Credit && it.walletId == walletId }
            return if (isDuplicateTransaction) {
                QueryResponse.Error.DuplicateTransaction(getWallet(walletId)!!)
            } else {
                addNewTransaction(
                    walletId,
                    TransactionType.Credit,
                    amount,
                    transactionId,
                    wallet.balance.version + 1,
                    wallet.balance.coins + amount
                )
                QueryResponse.Success(getWallet(walletId)!!)
            }
        }
    }

    fun debitWallet(walletId: UUID, amount: Int, transactionId: String): QueryResponse {
        val wallet = getWallet(walletId)!!

        return if (wallet.balance.coins < amount) {
            QueryResponse.Error.InsufficientFunds()
        } else {
            val isDuplicateTransaction: Boolean =
                transactions.any { it.id == transactionId && it.type == TransactionType.Debit && it.walletId == walletId }

            if (isDuplicateTransaction) {
                QueryResponse.Error.DuplicateTransaction(wallet)
            } else {
                addNewTransaction(
                    walletId,
                    TransactionType.Debit,
                    amount,
                    transactionId,
                    wallet.balance.version + 1,
                    wallet.balance.coins - amount
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
        balance: Int,
    ) {
        val newTransaction = Transaction(walletId, transactionType, coins, transactionId, version, balance)
        transactions.add(newTransaction)
    }

    fun getWallet(walletId: UUID): Wallet? {
        val transactionCheck = transactions.find { it.walletId == walletId }
        return if (transactionCheck == null) {
            null
        } else {
            val lastTransaction = getLatestTransaction(walletId)

            if (lastTransaction == null) {
                null
            } else {
                Wallet(
                    walletId,
                    Balance(lastTransaction.id, lastTransaction.version, lastTransaction.balance)
                )
            }
        }
    }

    private fun getLatestTransaction(walletId: UUID): Transaction? {
        return transactions.filter { it.walletId == walletId }.maxByOrNull { it.version }
    }
}

fun doesWalletExist(walletId: UUID): QueryResponse {
    val walletManager = WalletManager()
    val wallet = walletManager.getWallet(walletId)

    return if (wallet == null) {
        QueryResponse.Error.WalletNotFound()
    } else {
        QueryResponse.Success(wallet)
    }
}

