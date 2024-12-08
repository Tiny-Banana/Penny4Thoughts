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
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.utility.BalanceCalculator
import com.mobdeve.s12.group4.mco.utility.Filter
import java.text.DecimalFormat
import java.util.Calendar

class BudgetFragment(private val budgetAdapter: ParentAdapter<CategoryParent, Category>,
                     private val filter: Filter)
    : Fragment(R.layout.fragment_budget) {

    private lateinit var budgetRV: RecyclerView
    private lateinit var date: TextView
    private lateinit var budgetTV: TextView
    private lateinit var spentTV: TextView
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

        filterByMonth(currentMonth, currentYear)

        updateBudget()
    }

    private fun initializeViews(view: View) {
        budgetRV = view.findViewById(R.id.budgetRV)
        budgetTV = view.findViewById(R.id.totalBudget)
        spentTV = view.findViewById(R.id.totalSpent)
        ivBracketLeft = view.findViewById(R.id.ivBracket1)
        ivBracketRight = view.findViewById(R.id.ivBracket2)
        date = view.findViewById(R.id.date)
    }

    private fun setupRecyclerView() {
        budgetRV.adapter = budgetAdapter
        budgetRV.layoutManager = LinearLayoutManager(requireContext())
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
    }

    private fun filterByMonth(currentMonth: Int, currentYear: Int) {
        selectedMonthName = monthNames[currentMonth].substring(0, 3)
        date.text = "$selectedMonthName $currentYear"
        filter.setSelectedDate(selectedMonthName, currentYear)
        filter.filterCategoryBudget(currentMonth, currentYear, budgetAdapter)
        updateBudget()
    }

    fun updateBudget() {
        if (!this::budgetRV.isInitialized) return

        val transacParentList = budgetAdapter.list
        val balanceCalc = BalanceCalculator(transacParentList)
        val decimalFormat = DecimalFormat("#,##0.00")
        budgetTV.text = decimalFormat.format(balanceCalc.calculateBudget(filter.selectedMonthNum, filter.selectedYear))
        spentTV.text = decimalFormat.format(balanceCalc.calculateSpent(filter.selectedMonthNum, filter.selectedYear))
    }
}