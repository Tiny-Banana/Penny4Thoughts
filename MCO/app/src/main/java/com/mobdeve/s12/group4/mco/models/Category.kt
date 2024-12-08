package com.mobdeve.s12.group4.mco.models

class Category(
    id: Int, imageId: Int, name: String, type: String,
    transactions: MutableList<Transaction> = mutableListOf(),
    budgets: MutableList<Budget> = mutableListOf()
) {
    var id = id
        private set

    var imageId = imageId
        private set

    var name = name
        private set

    var type = type
        private set

    var transactions = transactions
        private set

    var budgets = budgets
        private set

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }

    fun addBudget(budget: Budget) {
        budgets.add(budget)
    }

    fun getBudgetForMonth(month: Int, year: Int): Budget? {
        return budgets.find { it.month == month && it.year == year }
    }
}