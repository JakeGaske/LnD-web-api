package com.example

import kotlin.test.*
import vgw.wallet.*

class ApplicationTest {
    // Wont have users soon so can remove once that is done
    @Test
    fun TestCreateUser() {
        users.clear()
        createNewUser("test")
        assertEquals("test", users[0].id)
    }

    @Test
    fun TestWillGetUserBalance() {
        users.clear()
        val bal = getUserBalance("testingUser")
        assertNotNull(bal)
    }

    @Test
    fun TestWillCreditWalletSuccessfully() {
        users.clear()
        createNewUser("test")
        assertNotNull(users[0])
        assertEquals(WalletResponse.Created, users[0].creditWallet(100, "test01"))
    }

    @Test
    fun TestWillCreditWithDuplicatesGetWalletResponseForBoth() {
        users.clear()
        createNewUser("test")
        assertEquals(WalletResponse.Created, users[0].creditWallet(100, "test01"))
        assertEquals(WalletResponse.DuplicateTransaction, users[0].creditWallet(100, "test01"))
    }

    @Test
    fun TestWillCheckDebitWalletSuccessfully() {
        users.clear()
        createNewUser("test")
        val user = users[0]
        user.creditWallet(100, "test01")
        assertEquals(WalletResponse.Created, user.debitWallet(100, "test01"))
    }

    @Test
    fun TestWillDebitMoreThanBalanceHasAndReturnError() {
        users.clear()
        createNewUser("test")
        val user = users[0]
        user.creditWallet(100, "test01")
        assertEquals(WalletResponse.InputError, user.debitWallet(101, "test01"))
    }

    @Test
    fun TestWillDebitWithDuplicatesGetWalletResponseForBoth() {
        users.clear()
        createNewUser("test")
        val user = users[0]
        user.creditWallet(100, "test01")
        assertEquals(WalletResponse.Created, user.debitWallet(100, "test01"))
        user.creditWallet(100, "test02")
        assertEquals(WalletResponse.DuplicateTransaction, user.debitWallet(100, "test01"))
    }
}
