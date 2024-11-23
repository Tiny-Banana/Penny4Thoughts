package com.mobdeve.s12.group4.mco.models

class Account(id: Long, imageId: Int, name: String, balance: Double, transactions: MutableList<Transaction>) {
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

    fun setBalance(balance: Double) {
        this.balance = balance
    }
}