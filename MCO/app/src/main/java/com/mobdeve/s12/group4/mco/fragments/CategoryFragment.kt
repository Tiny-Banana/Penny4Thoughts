package com.mobdeve.s12.group4.mco.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.adapters.ParentAdapter
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent

class CategoryFragment(private val categoryAdapter: ParentAdapter<CategoryParent, Category>) : Fragment(R.layout.fragment_category) {
    private lateinit var categoryRV: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryRV = view.findViewById(R.id.categoryRV)

        // Use the passed adapter
        categoryRV.adapter = categoryAdapter
        categoryRV.layoutManager = LinearLayoutManager(requireContext())
    }
}