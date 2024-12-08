package com.mobdeve.s12.group4.mco.utility

import android.util.Log
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.Account
import com.mobdeve.s12.group4.mco.models.Budget
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.models.CustomDate
import com.mobdeve.s12.group4.mco.models.IconItem
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction

class DataGenerator {
    companion object {
        // Define Categories
        private val salary = Category(0, R.drawable.wallet, "Salary", "Income")
        private val grants = Category(1, R.drawable.giftbox, "Grants", "Income")
        private val health = Category(3, R.drawable.health, "Health", "Expense")
        private val education = Category(2, R.drawable.education, "Education", "Expense")
        private val food = Category(4, R.drawable.food, "Food", "Expense")

        // Define Accounts
        private val accountChecking = Account(0, R.drawable.card, "Checking Account", 1000.0, mutableListOf())
        private val accountSavings = Account(1, R.drawable.pig, "Savings Account", 5000.0, mutableListOf())
        private val accountCash = Account(2, R.drawable.cash, "Cash", 200.0, mutableListOf())

        // Define Transactions
        private val transaction1 = Transaction(0, 50.0, "Expense", education, accountCash, "Just graduated!", CustomDate(2024, 0, 10))
        private val transaction2 = Transaction(1, 200.0, "Expense", food, accountSavings, "Eat healthy", CustomDate(2024, 10, 12))
        private val transaction3 = Transaction(2, 500.0, "Income", salary, accountChecking, "Paycheck day!", CustomDate(2024, 10, 23))
        private val transaction4 = Transaction(3, 20.0, "Expense", health, accountCash, "Gym break!", CustomDate(2024, 10, 23))
        private val transaction5 = Transaction(4, 20.0, "Income", grants, accountCash, "Won this gift card!", CustomDate(2024, 10, 23))
        private val transaction6 = Transaction(5, 20.0, "Expense", education, accountCash, "Won this gift card!", CustomDate(2024, 10, 23))
        private val transaction7 = Transaction(6, 20.0, "Expense", education, accountCash, "Won this gift card!", CustomDate(2024, 11, 23))

        // Define Icons
        private val icon1 = IconItem(R.drawable.pig, "#FFAAB9")
        private val icon2 = IconItem(R.drawable.card, "#67D4FF")
        private val icon3 = IconItem(R.drawable.cash, "#A2CC8D")

        init {
            // Add transactions to categories
            education.addTransaction(transaction1)
            education.addTransaction(transaction6)
            education.addTransaction(transaction7)
            food.addTransaction(transaction2)
            salary.addTransaction(transaction3)
            health.addTransaction(transaction4)
            food.addTransaction(transaction5)

            // Add budgets to categories
            food.addBudget(Budget(limit = 500.0, spent = 200.0, remaining = 300.0, month = 10, year = 2024))
            education.addBudget(Budget(limit = 600.0, spent = 250.0, remaining = 350.0, month = 10, year = 2024))

            // Add transactions to accounts
            accountCash.addTransaction(transaction1)
            accountSavings.addTransaction(transaction2)
            accountChecking.addTransaction(transaction3)
            accountCash.addTransaction(transaction4)
            accountCash.addTransaction(transaction5)
        }

        // Function to generate account data
        fun generateAccountData(): ArrayList<Account> {
            return arrayListOf(accountChecking, accountSavings, accountCash)
        }

        // Function to generate transaction data
        fun generateTransactionData(): ArrayList<Transaction> {
            return arrayListOf(transaction1, transaction2, transaction3, transaction4, transaction5)
        }

        // Function to generate category data
        fun generateCategoryData(): ArrayList<Category> {
            return arrayListOf(salary, grants, food, education, health)
        }

        fun generateIcons(): ArrayList<IconItem> {
            return arrayListOf(icon1, icon2, icon3)
        }

        fun generateCategoryParent(): ArrayList<CategoryParent> {
            // List of categories
            val categories = listOf(salary, grants, education, health, food)

            // Group categories by their type (Income/Expense)
            val groupedCategories = categories.groupBy { it.type }

            // Transform the grouped data into a list of CategoryParent objects
            val categoryParents = groupedCategories.map { (type, categoryList) ->
                CategoryParent(section = "$type Categories", list = categoryList.toMutableList())
            }

            // Return the result as an ArrayList
            return ArrayList(categoryParents)
        }

        fun generateBudgetParent(month: Int, year: Int): ArrayList<CategoryParent> {
            // Sample categories
            val categories = listOf(salary, grants, education, health, food)

            // Split categories into budgeted and not budgeted for the given month
            val budgeted = mutableListOf<Category>()
            val notBudgeted = mutableListOf<Category>()

            categories.forEach { category ->
                val budgetForMonth = category.budgets.find { budget ->
                    budget.month == month && budget.year == year
                }
                if (budgetForMonth != null) {
                    budgeted.add(category)
                } else {
                    notBudgeted.add(category)
                }
            }

            // Create the category parent list
            val categoryParent = arrayListOf(
                CategoryParent(section = "Budgeted categories", list = budgeted),
                CategoryParent(section = "Not Budgeted categories", list = notBudgeted)
            )

            return categoryParent
        }

        fun generateTransacParent(): ArrayList<TransacParent> {
            // List of all transactions
            val transactions = listOf(
                transaction1, transaction2, transaction3, transaction4, transaction5, transaction6, transaction7
            )

            // Group transactions by the formatted date (using CustomDate.toStringFull() or any date format you prefer)
            val groupedTransactions = transactions.groupBy { it.createdAt.toStringFull() }

            // Map the grouped data into TransacParent objects
            val transacParents = groupedTransactions.map { (date, transactionList) ->
                TransacParent(section = date, transactions = transactionList.toMutableList())
            }

            Log.d("GroupedTransactions", "Grouped Transactions: $groupedTransactions")

            return ArrayList(transacParents)
        }
    }
}
