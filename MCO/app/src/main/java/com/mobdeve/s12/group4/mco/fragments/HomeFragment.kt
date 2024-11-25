package com.mobdeve.s12.group4.mco.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.adapters.AccountAdapter
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.utility.BalanceCalculator
import java.text.DecimalFormat


class HomeFragment(private val accountAdapter: AccountAdapter) : Fragment(R.layout.fragment_home) {
    private lateinit var accountsRV: RecyclerView
    private lateinit var incomeTV: TextView
    private lateinit var expenseTV: TextView
    private lateinit var balanceTV: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountsRV = view.findViewById(R.id.accountsRV)
        incomeTV = view.findViewById(R.id.totalIncome)
        expenseTV = view.findViewById(R.id.totalExpense)
        balanceTV = view.findViewById(R.id.totalBalance)

        // Use the passed adapter
        accountsRV.adapter = accountAdapter
        accountsRV.layoutManager = LinearLayoutManager(requireContext())

        updateBalance()
    }

    fun updateBalance() {
        val decimalFormat = DecimalFormat("#,##0.00")
        val balanceCalc = BalanceCalculator(accountAdapter.accounts)
        expenseTV.text = decimalFormat.format(balanceCalc.calculateTotalExpenses())
        incomeTV.text = decimalFormat.format(balanceCalc.calculateTotalIncome())
        balanceTV.text = decimalFormat.format(balanceCalc.calculateTotalBalance())
    }
}