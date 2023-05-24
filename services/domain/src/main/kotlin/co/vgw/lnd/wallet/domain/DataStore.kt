package co.vgw.lnd.wallet.domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

// Off load this to a database when task up to task
val transactions = mutableListOf<Transaction>()

@Serializable
data class Transaction(
    @Serializable(with = UUIDSerializer::class)
    val walletId: UUID,
    val type: TransactionType,
    val amount: Int,
    val id: String,
    val version: Int,
    val balance: Int,
)

enum class TransactionType {
    Credit,
    Debit
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }
}