package com.mobdeve.s12.group4.mco.models

class Account(id: Int, imageId: Int, name: String, balance: Float, transactions: MutableList<Transaction>) {
    var id = id
        private set

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

    fun setBalance(balance: Float) {
        this.balance = balance
    }
}