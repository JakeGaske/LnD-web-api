package vgw.wallet

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(val transactionType: TransactionType, val coins: Int, val transactionId: String, val transactionVersion: Int)
@Serializable
data class Balance(var transactionId: String, var version: Int, var coins: Int)

enum class TransactionType{
    Credit,
    Debit
}