package com.mobdeve.s12.group4.mco.utility

import com.mobdeve.s12.group4.mco.models.Account
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction

class BalanceCalculator<T : Any>(private val transacsSource: List<T>) {
    private val allTransactions: List<Transaction>
        get() = transacsSource.flatMap {
            when (it) {
                is TransacParent -> it.transactions
                is Account -> it.transactions
                is Category -> it.transactions
                is Transaction -> listOf(it)
                else -> emptyList()
            }
        }

    fun calculateTotalBalance(): Double {
        return allTransactions.sumOf { transaction ->
            if (transaction.type == "Income") transaction.amount else -transaction.amount
        }
    }

    fun calculateTotalIncome(): Double {
        return allTransactions.filter { it.type == "Income" }.sumOf { it.amount }
    }

    fun calculateTotalExpenses(): Double {
        return allTransactions.filter { it.type == "Expense" }.sumOf { it.amount }
    }

    fun calculateBudget(month: Int, year: Int): Double {
        val categories = transacsSource.flatMap {
            when (it) {
                is CategoryParent -> it.list // Extract list from CategoryParent
                else -> emptyList() // Handle other types or empty cases
            }
        }

        // Get all budgets for the specified month and year
        val totalBudget = categories
            .mapNotNull { it.getBudgetForMonth(month, year) } // Get budgets for the month and year
            .sumOf { it.limit } // Sum up the limits of the budgets

        return totalBudget // Return the calculated total budget
    }

    fun calculateSpent(month: Int, year: Int): Double {
        val categories = transacsSource.flatMap {
            when (it) {
                is CategoryParent -> it.list // Extract list from CategoryParent
                else -> emptyList() // Handle other types or empty cases
            }
        }

        // Get all budgets for the specified month and year
        val totalSpent = categories
            .mapNotNull { it.getBudgetForMonth(month, year) } // Get budgets for the month and year
            .sumOf { it.spent } // Sum up the limits of the budgets
        return totalSpent
    }
}