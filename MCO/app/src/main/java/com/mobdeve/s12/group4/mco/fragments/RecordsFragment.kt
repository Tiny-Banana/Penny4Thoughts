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
import com.mobdeve.s12.group4.mco.utility.BalanceCalculator
import com.mobdeve.s12.group4.mco.utility.Filter
import java.text.DecimalFormat
import java.util.Calendar

class RecordsFragment(private val recordsAdapter: ParentAdapter<TransacParent, Transaction>,
                      private val filter : Filter)
                        : Fragment(R.layout.fragment_records) {

    private lateinit var expenseTV: TextView
    private lateinit var incomeTV: TextView
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

        initializeViews(view)
        setupRecyclerView()
        setupDateNavigation()


        // Initialize current month and year from the system
        val calendar = Calendar.getInstance()
        currentMonth = calendar.get(Calendar.MONTH) // 0-based month (January = 0)
        currentYear = calendar.get(Calendar.YEAR)

        // Display transactions for the current month and year
        filterByMonth(currentMonth, currentYear)
        updateBalance()
    }

    private fun initializeViews(view: View) {
        expenseTV = view.findViewById(R.id.monthlyExpense)
        incomeTV = view.findViewById(R.id.monthlyIncome)
        recordsRV = view.findViewById(R.id.recordsRV)
        ivBracketLeft = view.findViewById(R.id.ivBracket1)
        ivBracketRight = view.findViewById(R.id.ivBracket2)
        date = view.findViewById(R.id.date)
    }

    private fun setupRecyclerView() {
        recordsRV.adapter = recordsAdapter
        recordsRV.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupDateNavigation() {
        ivBracketLeft.setOnClickListener {
            navigateMonth(-1)
        }

        ivBracketRight.setOnClickListener {
            navigateMonth(1)
        }
    }

    private fun navigateMonth(monthDelta: Int) {
        currentMonth += monthDelta
        if (currentMonth < 0) {
            currentMonth = 11
            currentYear -= 1
        } else if (currentMonth > 11) {
            currentMonth = 0
            currentYear += 1
        }
        filterByMonth(currentMonth, currentYear)
        updateBalance()
    }

    private fun filterByMonth(currentMonth: Int, currentYear: Int) {
        selectedMonthName = monthNames[currentMonth].substring(0, 3)
        date.text = "$selectedMonthName $currentYear"
        filter.setSelectedDate(selectedMonthName, currentYear)
        filter.applyFilter(selectedMonthName,
            currentYear,
            recordsAdapter.originalList,
            recordsAdapter)

        if (recordsAdapter.list.isEmpty()) {
            view?.findViewById<TextView>(R.id.recordNoDataText)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<TextView>(R.id.recordNoDataText)?.visibility = View.GONE
        }
    }

    fun updateBalance() {
        if (!this::recordsRV.isInitialized) return

        val transacParentList = recordsAdapter.list
        val balanceCalc = BalanceCalculator(transacParentList)
        val decimalFormat = DecimalFormat("#,##0.00")
        expenseTV.text = decimalFormat.format(balanceCalc.calculateTotalExpenses())
        incomeTV.text = decimalFormat.format(balanceCalc.calculateTotalIncome())
    }
}