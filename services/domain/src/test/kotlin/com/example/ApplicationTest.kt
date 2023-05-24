package com.example

import co.vgw.lnd.wallet.domain.QueryResponse
import co.vgw.lnd.wallet.domain.WalletManager
import co.vgw.lnd.wallet.domain.transactions
import kotlin.test.*
import java.util.UUID

class ApplicationTest {
    private val walletManager = WalletManager()

    @Test
    fun `test Will Not Find Unknown Wallet`() {
        val walletId = UUID.randomUUID()

        when (val result = walletManager.doesWalletExist(walletId)) {
            is QueryResponse.Error.WalletNotFound -> {}
            else -> {
                fail("Expected QueryResponse.WalletNotFound but received: $result")
            }
        }
    }

    @Test
    fun `test Will Credit And Create A New Wallet`() {
        val walletId = UUID.randomUUID()

        when (val result: QueryResponse = walletManager.creditWallet(walletId, 100, "testID")) {
            is QueryResponse.Success -> {
                assertEquals(100, result.balance.coins)
                assertEquals("testID", result.balance.transactionId)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Credit Account`() {
        val walletId = UUID.randomUUID()

        walletManager.creditWallet(walletId, 1, "testID1")
        when (val result = walletManager.creditWallet(walletId, 2, "testID2")) {
            is QueryResponse.Success -> {
                assertEquals(3, result.balance.coins)
                assertEquals("testID2", result.balance.transactionId)
                assertEquals(2, transactions.filter { it.walletId == walletId }.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Credit One After Another`() {
        val walletId = UUID.randomUUID()

        walletManager.creditWallet(walletId, 123, "testID123")
        walletManager.creditWallet(walletId, 312, "testID312")
        walletManager.creditWallet(walletId, 456, "testID456")
        walletManager.creditWallet(walletId, 654, "testID654")

        when (val result = walletManager.creditWallet(walletId, 200, "test200")) {
            is QueryResponse.Success -> {
                assertEquals(1745, result.balance.coins)
                assertEquals("test200", result.balance.transactionId)
                assertEquals(5, transactions.filter { it.walletId == walletId }.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Check If Accepted Is Returned For Duplicate Credits`() {
        val walletId = UUID.randomUUID()

        walletManager.creditWallet(walletId, 123, "testID123")
        when (val result = walletManager.creditWallet(walletId, 123, "testID123")) {
            is QueryResponse.Error.DuplicateTransaction -> {
                assertEquals(123, result.balance.coins)
                assertEquals("testID123", result.balance.transactionId)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Debit Successfully`() {
        val walletId = UUID.randomUUID()

        walletManager.creditWallet(walletId, 1000, "testID123")
        when (val result = walletManager.debitWallet(walletId, 100, "testID123")) {
            is QueryResponse.Success -> {
                assertEquals(900, result.balance.coins)
                assertEquals("testID123", result.balance.transactionId)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Debit Successfully Multiple Times`() {
        val walletId = UUID.randomUUID()

        walletManager.creditWallet(walletId, 1000, "testID1")
        walletManager.debitWallet(walletId, 100, "testID2")
        walletManager.debitWallet(walletId, 100, "testID3")
        walletManager.debitWallet(walletId, 100, "testID4")
        when (val result = walletManager.debitWallet(walletId, 100, "testID5")) {
            is QueryResponse.Success -> {
                assertEquals(600, result.balance.coins)
                assertEquals("testID5", result.balance.transactionId)
                assertEquals(5, transactions.filter { it.walletId == walletId }.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun testWillDebitWithDuplicates() {
        val walletId = UUID.randomUUID()

        walletManager.creditWallet(walletId, 1000, "testID1")
        walletManager.debitWallet(walletId, 100, "testID2")
        when (val result = walletManager.debitWallet(walletId, 100, "testID2")) {
            is QueryResponse.Error.DuplicateTransaction -> {
                assertEquals(900, result.balance.coins)
                assertEquals("testID2", result.balance.transactionId)
                assertEquals(2, transactions.filter { it.walletId == walletId }.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }

    @Test
    fun `test Will Debit Fail When Debiting More Than Balance`() {
        val walletId = UUID.randomUUID()

        walletManager.creditWallet(walletId, 1000, "testID1")
        when (val result = walletManager.debitWallet(walletId, 1001, "testID2")) {
            is QueryResponse.Error.InsufficientFunds -> {
                assertEquals(1, transactions.filter { it.walletId == walletId }.size)
            }

            else -> {
                fail("Expected QueryResponse.Success but received: $result")
            }
        }
    }
}
