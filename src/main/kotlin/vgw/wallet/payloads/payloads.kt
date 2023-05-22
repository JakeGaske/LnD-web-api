package vgw.wallet.payloads

import kotlinx.serialization.Serializable

@Serializable
data class TransactionPayload(
    val transactionId: String,
    val coins: Int,
)