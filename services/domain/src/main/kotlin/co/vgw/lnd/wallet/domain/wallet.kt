package co.vgw.lnd.wallet.domain

import kotlinx.serialization.Serializable
import java.util.UUID

class WalletManager {
    @Serializable
    data class Balance(var transactionId: String, var version: Int, var coins: Int)

    fun creditWallet(walletId: UUID, amount: Int, transactionId: String): QueryResponse {
        val balance = getBalance(walletId)

        if (balance == null) {
            addNewTransaction(
                walletId,
                TransactionType.Credit,
                amount,
                transactionId,
                0,
                amount
            )
            return QueryResponse.Success(getBalance(walletId)!!)
        } else {
            val isDuplicateTransaction: Boolean =
                transactions.any { it.id == transactionId && it.type == TransactionType.Credit && it.walletId == walletId }
            return if (isDuplicateTransaction) {
                QueryResponse.Error.DuplicateTransaction(getBalance(walletId)!!)
            } else {
                addNewTransaction(
                    walletId,
                    TransactionType.Credit,
                    amount,
                    transactionId,
                    balance.version + 1,
                    balance.coins + amount
                )
                QueryResponse.Success(getBalance(walletId)!!)
            }
        }
    }

    fun debitWallet(walletId: UUID, amount: Int, transactionId: String): QueryResponse {
        val balance = getBalance(walletId)!!

        return if (balance.coins < amount) {
            QueryResponse.Error.InsufficientFunds()
        } else {
            val isDuplicateTransaction: Boolean =
                transactions.any { it.id == transactionId && it.type == TransactionType.Debit && it.walletId == walletId }

            if (isDuplicateTransaction) {
                QueryResponse.Error.DuplicateTransaction(balance)
            } else {
                addNewTransaction(
                    walletId,
                    TransactionType.Debit,
                    amount,
                    transactionId,
                    balance.version + 1,
                    balance.coins - amount
                )
                QueryResponse.Success(getBalance(walletId)!!)
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

    private fun getBalance(walletId: UUID): Balance? {
        val transactionCheck = transactions.find { it.walletId == walletId }
        return if (transactionCheck == null) {
            null
        } else {
            val lastTransaction = getLatestTransaction(walletId)

            if (lastTransaction == null) {
                null
            } else {
                Balance(lastTransaction.id, lastTransaction.version, lastTransaction.balance)
            }
        }
    }

    private fun getLatestTransaction(walletId: UUID): Transaction? {
        return transactions.filter { it.walletId == walletId }.maxByOrNull { it.version }
    }

    fun doesWalletExist(walletId: UUID): QueryResponse {
        val wallet = getBalance(walletId)

        return if (wallet == null) {
            QueryResponse.Error.WalletNotFound()
        } else {
            QueryResponse.Success(wallet)
        }
    }
}

