package vgw.wallet

enum class WalletResponse {
    DuplicateTransaction,
    Created,
    InputError,
    Ok,
    NotFound,
}


data class BalanceWalletResponse(var response: WalletResponse, var balance: Balance)

sealed interface QueryResponse {
    data class Success(val wallet: Wallet) : QueryResponse
    data class WalletNotFound(val msg: String = "Wallet Not Found") : QueryResponse
    data class InsufficientFunds(val msg: String = "Not Enough Funds") : QueryResponse
    data class DuplicateTransaction(val wallet: Wallet) : QueryResponse
}