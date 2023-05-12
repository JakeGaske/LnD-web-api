package com.example

import kotlin.test.*
import vgw.wallet.*

class ApplicationTest {
    @Test
    fun TestCreateUser() {
        users.clear()
        createNewUser("test")
        assertEquals("test", users[0].id)
    }

    @Test
    fun TestGetUserBalance() {
        users.clear()
        val bal = getUserBalance("testingUser")
        assertNotNull(bal)
    }

    @Test
    fun TestCreditAccount() {
        users.clear()
        createNewUser("test")
        assertNotNull(users[0])
        assertEquals(WalletResponse.Created, users[0].creditWallet(100, "test01"))
    }

    @Test
    fun TestCheckCreditResponseWithDuplicates() {
        users.clear()
        createNewUser("test")
        assertEquals(WalletResponse.Created, users[0].creditWallet(100, "test01"))
        assertEquals(WalletResponse.DuplicateTransaction, users[0].creditWallet(100, "test01"))
    }

    @Test
    fun CheckDebitResponse() {
        users.clear()
        createNewUser("test")
        val user = users[0]
        user.creditWallet(100, "test01")
        assertEquals(WalletResponse.Created, user.debitWallet(100, "test01"))
    }

    @Test
    fun DebitMoreThanBalanceHas() {
        users.clear()
        createNewUser("test")
        val user = users[0]
        user.creditWallet(100, "test01")
        assertEquals(WalletResponse.InputError, user.debitWallet(101, "test01"))
    }

    @Test
    fun CheckDebitResponseWithDuplicates() {
        users.clear()
        createNewUser("test")
        val user = users[0]
        user.creditWallet(100, "test01")
        assertEquals(WalletResponse.Created, user.debitWallet(100, "test01"))
        user.creditWallet(100, "test02")
        assertEquals(WalletResponse.DuplicateTransaction, user.debitWallet(100, "test01"))
    }
}
