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

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }
}