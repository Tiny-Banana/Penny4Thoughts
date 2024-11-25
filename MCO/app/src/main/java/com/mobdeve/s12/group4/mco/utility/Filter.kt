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

        fun applyFilter(
            month: String,
            year: Int,
            parentList: ArrayList<TransacParent>,
            adapter: ParentAdapter<TransacParent, Transaction>
        ): List<TransacParent> {
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
            return filteredTransacParents
        }

}