package co.vgw.lnd.wallet.app

import kotlinx.serialization.Serializable

@Serializable
data class TransactionPayload(
    val transactionId: String,
    val coins: Int,
)