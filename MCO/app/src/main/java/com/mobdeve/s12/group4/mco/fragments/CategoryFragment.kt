package com.mobdeve.s12.group4.mco.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.adapters.ParentAdapter
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.utility.BalanceCalculator
import java.text.DecimalFormat

class CategoryFragment(private val categoryAdapter: ParentAdapter<CategoryParent, Category>) : Fragment(R.layout.fragment_category) {
    private lateinit var categoryRV: RecyclerView
    private lateinit var incomeTV: TextView
    private lateinit var expenseTV: TextView
    private lateinit var balanceTV: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryRV = view.findViewById(R.id.categoryRV)
        incomeTV = view.findViewById(R.id.totalIncome)
        expenseTV = view.findViewById(R.id.totalExpense)
        balanceTV = view.findViewById(R.id.totalBalance)

        // Use the passed adapter
        categoryRV.adapter = categoryAdapter
        categoryRV.layoutManager = LinearLayoutManager(requireContext())

        updateBalance()
    }

    fun updateBalance() {
        if (!this::categoryRV.isInitialized) return

        val decimalFormat = DecimalFormat("#,##0.00")
        val balanceCalc = BalanceCalculator(categoryAdapter.getChildList())
        expenseTV.text = decimalFormat.format(balanceCalc.calculateTotalExpenses())
        incomeTV.text = decimalFormat.format(balanceCalc.calculateTotalIncome())
        balanceTV.text = decimalFormat.format(balanceCalc.calculateTotalBalance())
    }
}