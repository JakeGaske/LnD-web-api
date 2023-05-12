package com.example

import kotlin.test.*
import vgw.wallet.*

class ApplicationTest {
    @Test
    fun TestCreateUser() {
        users.clear()
        createNewUser("test")
        assertEquals(users[0].id, "test")
    }

    @Test
    fun GetUserBalance() {
        users.clear()
        val bal = getUserBalance("testingUser")
        assertNotNull(bal)
    }

    @Test
    fun CreditAccount() {
        users.clear()
        createNewUser("test")
        assertNotNull(users[0])
        assertEquals(users[0].creditWallet(100, "test01"), WalletResponse.Created)
    }

    @Test
    fun CheckCreditResponseWithDuplicates() {
        users.clear()
        createNewUser("test")
        assertEquals(users[0].creditWallet(100, "test01"), WalletResponse.Created)
        assertEquals(users[0].creditWallet(100, "test01"), WalletResponse.DuplicateTransaction)
    }

    @Test
    fun CheckDebitResponse() {
        users.clear()
        createNewUser("test")
        val user = users[0]
        user.creditWallet(100, "test01")
        assertEquals(user.debitWallet(100, "test01"), WalletResponse.Created)
    }

    @Test
    fun DebitMoreThanBalanceHas() {
        users.clear()
        createNewUser("test")
        val user = users[0]
        user.creditWallet(100, "test01")
        assertEquals(user.debitWallet(101, "test01"), WalletResponse.InputError)
    }

    @Test
    fun CheckDebitResponseWithDuplicates() {
        users.clear()
        createNewUser("test")
        val user = users[0]
        user.creditWallet(100, "test01")
        assertEquals(user.debitWallet(100, "test01"), WalletResponse.Created)
        user.creditWallet(100, "test02")
        assertEquals(user.debitWallet(100, "test01"), WalletResponse.DuplicateTransaction)
    }
}
