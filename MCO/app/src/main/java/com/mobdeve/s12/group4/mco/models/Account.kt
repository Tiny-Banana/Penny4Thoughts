package com.mobdeve.s12.group4.mco.models

class Account(id: Long, imageId: Int, name: String, balance: Double, transactions: MutableList<Transaction>) {
    var id = id
        private set

    var imageId = imageId
        set(value) {
            field = value
        }

    var name = name
        set(value) {
            field = value
        }

    var balance = balance
        set(value) {
            field = value
        }

    var transactions = transactions
        private set

    fun updateTransaction(updatedTransaction: Transaction) {
        // Find the transaction by ID or any other unique identifier
        val index = transactions.indexOfFirst { it.id == updatedTransaction.id }
        if (index != -1) {
            // Update the transaction at the found index
            transactions[index] = updatedTransaction
        }
    }

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }
}