package vgw

import kotlinx.serialization.Serializable

// Off load this to a database when task up to task
val transactions = mutableListOf<Transaction>()

@Serializable
data class Transaction(
    val walletId: String,
    val type: TransactionType,
    val coins: Int,
    val id: String,
    val version: Int,
)

enum class TransactionType {
    Credit,
    Debit
}