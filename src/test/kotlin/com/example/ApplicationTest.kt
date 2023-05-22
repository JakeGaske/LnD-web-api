package com.example

import vgw.transactions
import kotlin.test.*
import vgw.wallet.*
import java.util.UUID

class ApplicationTest {
    @Test
    fun `test Will Not Find Unknown Wallet`() {
        val walletId = UUID.randomUUID().toString()
        when (val result = doesWalletExist(walletId)) {
            is QueryResponse.WalletNotFound -> {}
            else -> {
                fail("Expected QueryResponse.WalletNotFOund but received: $result")
            }
        }
    }

    @Test
    fun `test Will Credit And Create A New Wallet`() {
        val walletId = UUID.randomUUID().toString()

        when (val result: QueryResponse = creditWallet(walletId, 100, "testID")) {
            is QueryResponse.Success -> {
                assertEquals(100, result.wallet.balance.coins)
                assertEquals("testID", result.wallet.balance.transactionId)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Credit Account`() {
        val walletId = UUID.randomUUID().toString()

        creditWallet(walletId, 1, "testID1")
        when (val result = creditWallet(walletId, 2, "testID2")) {
            is QueryResponse.Success -> {
                assertEquals(3, result.wallet.balance.coins)
                assertEquals("testID2", result.wallet.balance.transactionId)
                assertEquals(2, transactions.filter { it.walletId == walletId }.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Credit One After Another`() {
        val walletId = UUID.randomUUID().toString()

        creditWallet(walletId, 123, "testID123")
        creditWallet(walletId, 312, "testID312")
        creditWallet(walletId, 456, "testID456")
        creditWallet(walletId, 654, "testID654")

        when (val result = creditWallet(walletId, 200, "test200")) {
            is QueryResponse.Success -> {
                assertEquals(1745, result.wallet.balance.coins)
                assertEquals("test200", result.wallet.balance.transactionId)
                assertEquals(5, transactions.filter { it.walletId == walletId }.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Check If Accepted Is Returned For Duplicate Credits`() {
        val walletId = UUID.randomUUID().toString()

        creditWallet(walletId, 123, "testID123")
        when (val result = creditWallet(walletId, 123, "testID123")) {
            is QueryResponse.DuplicateTransaction -> {
                assertEquals(123, result.wallet.balance.coins)
                assertEquals("testID123", result.wallet.balance.transactionId)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Debit Successfully`() {
        val walletId = UUID.randomUUID().toString()

        creditWallet(walletId, 1000, "testID123")
        when (val result = debitWallet(walletId, 100, "testID123")) {
            is QueryResponse.Success -> {
                assertEquals(900, result.wallet.balance.coins)
                assertEquals("testID123", result.wallet.balance.transactionId)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Debit Successfully Multiple Times`() {
        val walletId = UUID.randomUUID().toString()

        creditWallet(walletId, 1000, "testID1")
        debitWallet(walletId, 100, "testID2")
        debitWallet(walletId, 100, "testID3")
        debitWallet(walletId, 100, "testID4")
        when (val result = debitWallet(walletId, 100, "testID5")) {
            is QueryResponse.Success -> {
                assertEquals(600, result.wallet.balance.coins)
                assertEquals("testID5", result.wallet.balance.transactionId)
                assertEquals(5, transactions.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun testWillDebitWithDuplicates() {
        val walletId = UUID.randomUUID().toString()

        creditWallet(walletId, 1000, "testID1")
        debitWallet(walletId, 100, "testID2")
        when (val result = debitWallet(walletId, 100, "testID2")) {
            is QueryResponse.DuplicateTransaction -> {
                assertEquals(900, result.wallet.balance.coins)
                assertEquals("testID2", result.wallet.balance.transactionId)
                assertEquals(2, transactions.filter { it.walletId == walletId }.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Debit Fail When Debiting More Than Balance`() {
        val walletId = UUID.randomUUID().toString()

        creditWallet(walletId, 1000, "testID1")
        when (val result = debitWallet(walletId, 1001, "testID2")) {
            is QueryResponse.InsufficientFunds -> {
                assertEquals(1, transactions.filter { it.walletId == walletId }.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }
}
