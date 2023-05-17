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
val wallets = mutableListOf<Wallet>()

@Serializable
data class Wallet(val id: String, var balance: Balance, var transactions: MutableList<Transaction>)

fun creditWallet(walletID: String, coins: Int, transactionId: String): BalanceWalletResponse {
    val status = BalanceWalletResponse(WalletResponse.NotFound, Balance("NA", 0, 0))
    val wallet = getWallet(walletID)

    if (wallet == null) {
        // Create the wallet and credit it here
        val newWallet = Wallet(walletID, Balance(transactionId, 1, coins), mutableListOf())

        addNewTransaction(newWallet, TransactionType.Credit, coins, transactionId, newWallet.balance.version)

        wallets.add(newWallet)
        status.balance = newWallet.balance
        status.response = WalletResponse.Created
    } else {
        // When a wallet already exists do this
        val isDuplicateId: Boolean =
            wallet.transactions.any { it.transactionId == transactionId && it.transactionType == TransactionType.Credit }

        if (isDuplicateId) {
            status.response = WalletResponse.DuplicateTransaction
            status.balance = wallet.balance
        } else {
            wallet.balance.coins += coins
            wallet.balance.transactionId = transactionId
            wallet.balance.version += 1

            addNewTransaction(wallet, TransactionType.Credit, coins, transactionId, wallet.balance.version)

            status.response = WalletResponse.Created
        }
    }

    return status
}

fun debitWallet(walletID: String, coins: Int, transactionId: String): BalanceWalletResponse {
    val wallet = getWallet(walletID)
    val status = BalanceWalletResponse(WalletResponse.NotFound, Balance("NA", 0, 0))

    if (wallet == null) {
        status.response = WalletResponse.NotFound
    } else {
        if (wallet.balance.coins < coins) {
            status.response = WalletResponse.InputError
        } else {
            val isDuplicateId: Boolean =
                wallet.transactions.any { it.transactionId == transactionId && it.transactionType == TransactionType.Debit }
            if (isDuplicateId) {
                status.response = WalletResponse.DuplicateTransaction
                status.balance = wallet.balance
            } else {
                wallet.balance.coins -= coins
                wallet.balance.transactionId = transactionId
                wallet.balance.version += 1

                addNewTransaction(wallet, TransactionType.Debit, coins, transactionId, wallet.balance.version)

                status.response = WalletResponse.Created
            }
        }
    }

    return status
}

fun getWalletBalance(id: String): BalanceWalletResponse {
    val status = BalanceWalletResponse(WalletResponse.NotFound, Balance("NA", 0, 0))
    val wallet = getWallet(id)

    when (doesWalletExist(id)) {
        WalletResponse.Ok -> {
            status.balance = wallet!!.balance
            status.response = WalletResponse.Ok
        }

        else -> {}
    }

    return status
}

fun addNewTransaction(
    wallet: Wallet,
    transactionType: TransactionType,
    coins: Int,
    transactionId: String,
    version: Int,
) {
    val newTransaction = Transaction(transactionType, coins, transactionId, version)
    wallet.transactions.add(newTransaction)
}

fun getWallet(id: String): Wallet? {
    return wallets.find { it.id == id }
}

fun doesWalletExist(id: String): WalletResponse {
    val wallet: Wallet? = wallets.find { it.id == id }
    return if (wallet == null) {
        WalletResponse.NotFound
    } else {
        WalletResponse.Ok
    }
}