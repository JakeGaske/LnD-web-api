package co.vgw.lnd.wallet.domain

sealed interface QueryResponse {
    data class Success(val balance: WalletManager.Balance) : QueryResponse

    sealed interface Error : QueryResponse {
        data class WalletNotFound(val msg: String = "Wallet Not Found") : Error
        data class InsufficientFunds(val msg: String = "Not Enough Funds") : Error
        data class DuplicateTransaction(val balance: WalletManager.Balance) : Error
    }
}