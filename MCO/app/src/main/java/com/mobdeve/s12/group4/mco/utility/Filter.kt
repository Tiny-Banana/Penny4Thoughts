package com.mobdeve.s12.group4.mco.utility

import com.mobdeve.s12.group4.mco.adapters.ParentAdapter
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction

class Filter {
        var selectedMonth: String = "Jan"
        var selectedYear: Int = 0

        fun setSelectedDate(month: String, year: Int) {
            selectedMonth = month
            selectedYear = year
        }

        fun displayTransactionsForMonth(month: String,
                                        year: Int,
                                        transacParent: ArrayList<TransacParent>,
                                        adapter: ParentAdapter<TransacParent, Transaction>) {

            // Filter transacParents for the selected month and year
            val filteredTransacParents = transacParent.filter { parent ->
                // Extract the month and year from the section (e.g., "Jan 2024")
                val sectionParts = parent.section.split(" ")
                if (sectionParts.size >= 3) {
                    val sectionMonth = sectionParts[0] // Extract "Jan"
                    val sectionYear = sectionParts[2].toIntOrNull() // Extract year (e.g., "2024")
                    // Match the month abbreviation and year
                    sectionMonth == month && sectionYear == year
                } else {
                    false // Skip if the format is unexpected
                }
            }

            // Further filter by day and sort by day in ascending order
            val sortedFilteredTransacParents = filteredTransacParents.map { parent ->
                // Sort the child transactions by day of the month in ascending order
                val sortedChildren = parent.list.sortedBy { it.createdAt.day_in_month }
                // Create a new TransacParent object with sorted children
                parent.copy(list = ArrayList(sortedChildren))
            }
            // Sort the entire list of TransacParent objects based on the earliest transaction day
            val sortedTransacParentsByDay = sortedFilteredTransacParents.sortedBy { parent ->
                // Use the day of the first transaction (or the first child in the list)
                parent.list.firstOrNull()?.createdAt?.day_in_month ?: 0
            }

            // Update the adapter with the filtered and sorted transacParents
            adapter.updateFilteredList(ArrayList(sortedTransacParentsByDay))
    }
}