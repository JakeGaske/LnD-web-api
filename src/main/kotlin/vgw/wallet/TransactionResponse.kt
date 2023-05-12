package vgw.wallet

import io.ktor.http.*

enum class WalletResponse() {
    DuplicateTransaction,
    Created,
    InputError,
    Ok,
    NotFound,
}

fun ConvertWalletResponseToHTTPStatus(response: WalletResponse): HttpStatusCode {
    return when (response) {
        WalletResponse.Created -> HttpStatusCode.Created
        WalletResponse.InputError -> HttpStatusCode.BadRequest
        WalletResponse.DuplicateTransaction -> HttpStatusCode.Accepted
        WalletResponse.NotFound -> HttpStatusCode.NotFound
        WalletResponse.Ok -> HttpStatusCode.OK
    }
}