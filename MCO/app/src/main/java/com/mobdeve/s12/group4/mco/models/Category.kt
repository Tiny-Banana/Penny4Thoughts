package com.mobdeve.s12.group4.mco.models

class Category(imageId: Int, name: String, transactions: MutableList<Transaction>) {
    var imageId = imageId
        private set

    var name = name
        private set

    var transactions = transactions
        private set

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }
}