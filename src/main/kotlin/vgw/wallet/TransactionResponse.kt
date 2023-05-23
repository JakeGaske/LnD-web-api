package vgw.wallet

sealed interface QueryResponse {
    data class Success(val wallet: WalletManager.Wallet) : QueryResponse

    sealed interface Error : QueryResponse {
        data class WalletNotFound(val msg: String = "Wallet Not Found") : Error
        data class InsufficientFunds(val msg: String = "Not Enough Funds") : Error
        data class DuplicateTransaction(val wallet: WalletManager.Wallet) : Error
    }
}