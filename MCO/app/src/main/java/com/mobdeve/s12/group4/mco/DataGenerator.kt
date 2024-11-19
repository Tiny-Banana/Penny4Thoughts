package com.mobdeve.s12.group4.mco

import com.mobdeve.s12.group4.mco.models.Account
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CustomDate
import com.mobdeve.s12.group4.mco.models.IconItem
import com.mobdeve.s12.group4.mco.models.Transaction

class DataGenerator {
    companion object {
        // Define Categories
        private val salary = Category(R.drawable.cash, "Salary", mutableListOf())
        private val clothing = Category(R.drawable.pig, "Clothing", mutableListOf())
        private val health = Category(R.drawable.card, "Health", mutableListOf())

        // Define Accounts
        private val accountChecking = Account(R.drawable.card, "Checking Account", 1000.0f, mutableListOf())
        private val accountSavings = Account(R.drawable.pig, "Savings Account", 5000.0f, mutableListOf())
        private val accountCash = Account(R.drawable.cash, "Cash", 200.0f, mutableListOf())

        // Define Transactions
        private val transaction1 = Transaction(50.0f, "Expense", clothing, accountCash, "Loving these clothes", CustomDate(2023, 0, 10))
        private val transaction2 = Transaction(200.0f, "Expense", health, accountSavings, "Eat healthy", CustomDate(2024, 10, 12))
        private val transaction3 = Transaction(500.0f, "Income", salary, accountChecking, "Paycheck day!", CustomDate(2024, 10, 1))
        private val transaction4 = Transaction(20.0f, "Expense", health, accountCash, "Gym break!",
            CustomDate(2024, 10, 23)
        )

        // Define Icons
        private val icon1 = IconItem(R.drawable.pig, "#FFAAB9")
        private val icon2 = IconItem(R.drawable.card, "#67D4FF")
        private val icon3 = IconItem(R.drawable.cash, "#A2CC8D")

        init {
            // Add transactions to categories
            clothing.addTransaction(transaction1)
            health.addTransaction(transaction2)
            salary.addTransaction(transaction3)
            health.addTransaction(transaction4)

            // Add transactions to accounts
            accountCash.addTransaction(transaction1)
            accountSavings.addTransaction(transaction2)
            accountChecking.addTransaction(transaction3)
            accountCash.addTransaction(transaction4)
        }

        // Function to generate account data
        fun generateAccountData(): ArrayList<Account> {
            return arrayListOf(accountChecking, accountSavings, accountCash)
        }

        // Function to generate transaction data
        fun generateTransactionData(): ArrayList<Transaction> {
            return arrayListOf(transaction1, transaction2, transaction3, transaction4)
        }

        // Function to generate category data
        fun generateCategoryData(): ArrayList<Category> {
            return arrayListOf(salary, clothing, health)
        }

        fun generateIcons(): ArrayList<IconItem> {
            return arrayListOf(icon1, icon2, icon3)
        }
    }
}
