package vgw.wallet.payloads

import kotlinx.serialization.Serializable

@Serializable
data class CreditPayload(
    val transactionId: String,
    val coins: Int
)