package vgw.wallet

enum class WalletResponse() {
    DuplicateTransaction,
    Created,
    InputError,
    Ok,
    NotFound,
}


data class BalanceWalletResponse(var response: WalletResponse, var balance: Balance)