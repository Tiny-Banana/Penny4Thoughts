package com.mobdeve.s12.group4.mco.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.adapters.AccountAdapter
import com.mobdeve.s12.group4.mco.R


class HomeFragment(private val accountAdapter: AccountAdapter) : Fragment(R.layout.home_fragment) {
    private lateinit var accountsRV: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountsRV = view.findViewById(R.id.accountsRV)

        // Use the passed adapter
        accountsRV.adapter = accountAdapter
        accountsRV.layoutManager = LinearLayoutManager(requireContext())
    }
}