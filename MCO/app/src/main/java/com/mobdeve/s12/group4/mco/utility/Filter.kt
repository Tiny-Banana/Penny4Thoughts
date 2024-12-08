package com.mobdeve.s12.group4.mco.utility

import com.mobdeve.s12.group4.mco.adapters.AnalysisAdapter
import com.mobdeve.s12.group4.mco.adapters.ParentAdapter
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction
import java.util.Calendar

class Filter {
        private val monthMap = mapOf(
            "Jan" to 0, "Feb" to 1, "Mar" to 2, "Apr" to 3,
            "May" to 4, "Jun" to 5, "Jul" to 6, "Aug" to 7,
            "Sep" to 8, "Oct" to 9, "Nov" to 10, "Dec" to 11
        )
        private val reverseMonthMap = monthMap.entries.associate { it.value to it.key }

        val calendar = Calendar.getInstance()
        var selectedMonth: String = reverseMonthMap[calendar.get(Calendar.MONTH)].toString()
        var selectedMonthNum: Int = calendar.get(Calendar.MONTH)
        var selectedYear: Int = calendar.get(Calendar.YEAR)

        fun setSelectedDate(month: String, year: Int) {
            selectedMonth = month
            selectedMonthNum = monthMap[month] ?: 0
            selectedYear = year
        }

        // filter for transacparents in parentadapter
        fun applyFilter(
            month: String,
            year: Int,
            parentList: ArrayList<TransacParent>,
            adapter: ParentAdapter<TransacParent, Transaction>) {
            val filteredTransacParents = parentList.filter { parent ->
                val sectionParts = parent.section.split(" ")
                if (sectionParts.size >= 3) {
                    val sectionMonth = sectionParts[0]
                    val sectionYear = sectionParts[2].toIntOrNull()
                    sectionMonth == month && sectionYear == year
                } else {
                    false
                }
            }.map { parent ->
                val sortedChildren = parent.list.sortedBy { it.createdAt.day_in_month }
                parent.copy(list = ArrayList(sortedChildren))
            }.sortedBy { parent ->
                parent.list.firstOrNull()?.createdAt?.day_in_month ?: 0
            }

            adapter.updateFilteredList(ArrayList(filteredTransacParents))
        }

        // filter for categories in analysis adapter
        fun applyFilter(
            month: Int,
            year: Int,
            adapter: AnalysisAdapter,
        ) {
            val categoryList = adapter.categories
            val allFilteredTransactions = mutableListOf<Transaction>()
            val filteredDateTransactions = mutableListOf<Transaction>()

            categoryList.forEach { category ->
                // Filter transactions by month, year, and category type in a single pass
                category.transactions.forEach { transaction ->
                    val transactionMonth = transaction.createdAt.month // Assuming createdAt has month
                    val transactionYear = transaction.createdAt.year // Assuming createdAt has year

                    val isMonthYearMatch = transactionMonth == month && transactionYear == year

                    // If categoryType filter is provided, also check the category type
                    val isCategoryTypeMatch = adapter.selectedType.let { it == transaction.category.type }

                    // Keep transaction if both month/year match and category type (if provided) match
                    if (isMonthYearMatch && isCategoryTypeMatch) {
                        allFilteredTransactions.add(transaction)
                    }

                    if (isMonthYearMatch) {
                        filteredDateTransactions.add(transaction)
                    }
                }
            }

            adapter.filteredDateTransactions = filteredDateTransactions
            adapter.filteredTransactions = allFilteredTransactions
        }

    fun filterCategoryBudget(month: Int, year: Int, adapter: ParentAdapter<CategoryParent, Category>) {
        // Generate the new data based on the selected month and year
        val categories = adapter.list.flatMap { it.list }

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

        adapter .apply {
            list = categoryParent // Update the adapter's list with the new data
            notifyDataSetChanged() // Notify the adapter that the data has changed
        }
    }
}