package com.mobdeve.s12.group4.mco.utility

import com.mobdeve.s12.group4.mco.adapters.AnalysisAdapter
import com.mobdeve.s12.group4.mco.adapters.ParentAdapter
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction

class Filter {
        var selectedMonth: String = "Jan"
        var selectedYear: Int = 0

        fun setSelectedDate(month: String, year: Int) {
            selectedMonth = month
            selectedYear = year
        }

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
                val sortedChildren = parent.transactions.sortedBy { it.createdAt.day_in_month }
                parent.copy(transactions = ArrayList(sortedChildren))
            }.sortedBy { parent ->
                parent.transactions.firstOrNull()?.createdAt?.day_in_month ?: 0
            }

            adapter.updateFilteredList(ArrayList(filteredTransacParents))
        }

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
}