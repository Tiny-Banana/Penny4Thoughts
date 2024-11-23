package com.mobdeve.s12.group4.mco.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.adapters.ParentAdapter
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction
import com.mobdeve.s12.group4.mco.utility.Filter
import java.util.Calendar

class RecordsFragment(private val recordsAdapter: ParentAdapter<TransacParent, Transaction>,
                      private val filter : Filter)
                        : Fragment(R.layout.fragment_records) {

    private lateinit var recordsRV: RecyclerView
    private lateinit var date: TextView
    private lateinit var ivBracketLeft: ImageView
    private lateinit var ivBracketRight: ImageView

    private var currentMonth: Int = 0
    private var currentYear: Int = 0
    private var selectedMonthName = "Jan"

    private val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recordsRV = view.findViewById(R.id.recordsRV)
        ivBracketLeft = view.findViewById(R.id.ivBracket1)
        ivBracketRight = view.findViewById(R.id.ivBracket2)
        date = view.findViewById(R.id.date)

        // Initialize current month and year from the system
        val calendar = Calendar.getInstance()
        currentMonth = calendar.get(Calendar.MONTH) // 0-based month (January = 0)
        currentYear = calendar.get(Calendar.YEAR)

        // Set up RecyclerView
        recordsRV.adapter = recordsAdapter
        recordsRV.layoutManager = LinearLayoutManager(requireContext())

        // Display transactions for the current month and year
        selectedMonthName = monthNames[currentMonth].substring(0, 3)
        filter.displayTransactionsForMonth(selectedMonthName, currentYear, recordsAdapter.originalList, recordsAdapter)

        // Setup arrow click listeners for month navigation
        ivBracketLeft.setOnClickListener {
            // Navigate to the previous month
            currentMonth -= 1
            if (currentMonth < 0) { // Wrap to December of the previous year
                currentMonth = 11
                currentYear -= 1
            }
            filterByMonth(currentMonth, currentYear)
        }

        ivBracketRight.setOnClickListener {
            // Navigate to the next month
            currentMonth += 1
            if (currentMonth > 11) { // Wrap to January of the next year
                currentMonth = 0
                currentYear += 1
            }
            filterByMonth(currentMonth, currentYear)
        }
    }

    fun filterByMonth(currentMonth: Int, currentYear: Int) {
        selectedMonthName = monthNames[currentMonth].substring(0, 3)
        date.text = "$selectedMonthName $currentYear"
        filter.setSelectedDate(selectedMonthName, currentYear)
        filter.displayTransactionsForMonth(selectedMonthName, currentYear, recordsAdapter.originalList, recordsAdapter)
    }
}