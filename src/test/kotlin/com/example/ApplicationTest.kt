package com.example

import kotlin.test.*
import vgw.wallet.*

class ApplicationTest {

    @Test
    fun testWillNotFindUnknownWallet() {
        val walletID = "testWillNotFindUnknownWallet"

        val result = getWallet(walletID)
        assertNull(result)
    }


    @Test
    fun testWillCreditAndCreateANewWallet() {
        val walletID = "testWillCreditAndCreateANewWallet"

        val result = creditWallet(walletID, 100, "testID")
        assertEquals(WalletResponse.Created, result.response)
        assertEquals(100, result.balance.coins)
        assertEquals("testID", result.balance.transactionId)

        val resultWallet = getWallet(walletID)
        assertNotNull(resultWallet)
    }

    @Test
    fun testWillCreditAccount() {
        val walletID = "testWillCreditAccount"

        creditWallet(walletID, 1, "testID1")
        creditWallet(walletID, 2, "testID2")

        val result = getWallet(walletID)
        assertNotNull(result)

        assertEquals(3, result.balance.coins)
        assertEquals("testID2", result.balance.transactionId)
        assertEquals(2, result.transactions.size)
    }

    @Test
    fun testWillCreditOneAfterAnother() {
        val walletID = "testWillCreditOneAfterAnother"

        creditWallet(walletID, 123, "testID123")
        creditWallet(walletID, 312, "testID312")
        creditWallet(walletID, 456, "testID456")
        creditWallet(walletID, 654, "testID654")
        creditWallet(walletID, 200, "test200")

        val result = getWallet(walletID)
        assertNotNull(result)

        assertEquals(1745, result.balance.coins)
        assertEquals("test200", result.balance.transactionId)
        assertEquals(5, result.transactions.size)
    }

    @Test
    fun testWillCheckIfAcceptedIsReturnedForDuplicateCredits() {
        val walletID = "testWillCheckIfAcceptedIsReturnedForDuplicateCredits"

        creditWallet(walletID, 123, "testID123")
        val result = creditWallet(walletID, 123, "testID123")

        assertEquals(WalletResponse.DuplicateTransaction, result.response)
        assertEquals(123, result.balance.coins)
        assertEquals("testID123", result.balance.transactionId)
    }

    @Test
    fun testWillDebitSuccessfully() {
        val walletID = "testWillDebitSuccessfully"

        creditWallet(walletID, 1000, "testID123")
        debitWallet(walletID, 100, "testID123")

        val result = getWallet(walletID)
        assertNotNull(result)

        assertEquals(900, result.balance.coins)
        assertEquals("testID123", result.balance.transactionId)
    }

    @Test
    fun testWillDebitSuccessfullyMultipleTimes() {
        val walletID = "testWillDebitSuccessfullyMultipleTimes"

        creditWallet(walletID, 1000, "testID1")
        debitWallet(walletID, 100, "testID2")
        debitWallet(walletID, 100, "testID3")
        debitWallet(walletID, 100, "testID4")
        debitWallet(walletID, 100, "testID5")

        val result = getWallet(walletID)
        assertNotNull(result)

        assertEquals(600, result.balance.coins)
        assertEquals("testID5", result.balance.transactionId)
        assertEquals(5, result.transactions.size)
    }

    @Test
    fun testWillDebitWithDuplicates() {
        val walletID = "testWillDebitWithDuplicates"

        creditWallet(walletID, 1000, "testID1")
        debitWallet(walletID, 100, "testID2")
        val responseResult = debitWallet(walletID, 100, "testID2")
        assertEquals(WalletResponse.DuplicateTransaction, responseResult.response)

        val result = getWallet(walletID)
        assertNotNull(result)

        assertEquals(900, result.balance.coins)
        assertEquals("testID2", result.balance.transactionId)
        assertEquals(2, result.transactions.size)
    }

    @Test
    fun testWillDebitFailWhenDebitingMoreThanBalance() {
        val walletID = "testWillDebitFailWhenDebitingMoreThanBalance"

        creditWallet(walletID, 1000, "testID1")
        val responseResult = debitWallet(walletID, 1001, "testID2")
        assertEquals(WalletResponse.InputError, responseResult.response)

        val result = getWallet(walletID)
        assertNotNull(result)

        assertEquals(1000, result.balance.coins)
        assertEquals("testID1", result.balance.transactionId)
        assertEquals(1, result.transactions.size)
    }
}
