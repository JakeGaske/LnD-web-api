package vgw.wallet

sealed interface QueryResponse {
    data class Success(val wallet: Wallet) : QueryResponse
    data class WalletNotFound(val msg: String = "Wallet Not Found") : QueryResponse
    data class InsufficientFunds(val msg: String = "Not Enough Funds") : QueryResponse
    data class DuplicateTransaction(val wallet: Wallet) : QueryResponse
}