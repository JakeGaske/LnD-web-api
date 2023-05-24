package co.vgw.lnd.wallet.http

import kotlinx.serialization.Serializable

@Serializable
data class TransactionPayload(
    val transactionId: String,
    val coins: Int,
)

@Serializable
data class BalanceResponse(var transactionId: String, var version: Int, var coins: Int)