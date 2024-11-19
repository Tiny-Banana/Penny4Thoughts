package com.mobdeve.s12.group4.mco.models

class Account(imageId: Int, name: String, balance: Float, transactions: MutableList<Transaction>) {
    var imageId = imageId
        private set

    var name = name
        private set

    var balance = balance
        private set

    var transactions = transactions
        private set

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }
}