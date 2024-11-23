package com.mobdeve.s12.group4.mco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction
import com.mobdeve.s12.group4.mco.utility.Filter

class ParentAdapter<T : Any, U : Any>(
    private var list: ArrayList<T>,
    private val getSection: (T) -> String,
    private val getChildList: (T) -> List<U>,
    private val childAdapterFactory: (List<U>) -> RecyclerView.Adapter<*>,
    private val filter : Filter
) : RecyclerView.Adapter<ParentAdapter<T, U>.ParentHolder>() {

    var originalList = ArrayList<T>(list) // Store the original list

    inner class ParentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val section: TextView = itemView.findViewById(R.id.section)
        val childRV: RecyclerView = itemView.findViewById(R.id.child_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_parent, parent, false)
        return ParentHolder(view)
    }

    override fun onBindViewHolder(holder: ParentHolder, position: Int) {
        val item = list[position]  // Bind to the filtered list
        holder.section.text = getSection(item)
        holder.childRV.apply {
            adapter = childAdapterFactory(getChildList(item))
        }
    }

    override fun getItemCount(): Int = list.size

    fun addItem(item: U) {
        when (item) {
            is Transaction -> {
                // Cast the list to the correct type
                val parentList = originalList as ArrayList<TransacParent>

                // Extract the section name (e.g., "Feb 2024") from the transaction's createdAt
                val section = item.createdAt.toStringFull()

                // Find the existing parent for this section
                val existingParent = parentList.find { it.section == section }

                if (existingParent != null) {
                    // Add the transaction to the existing parent's child list
                    existingParent.list.add(item)

                } else {
                    // Create a new parent and add it to the list
                    val newParent = TransacParent(section, arrayListOf(item))
                    parentList.add(newParent)
                }

                // Pass the entire parent list
                filter.displayTransactionsForMonth(filter.selectedMonth,
                    filter.selectedYear,
                    parentList, // Pass a list of one item
                    this as ParentAdapter<TransacParent, Transaction>)
            }
        }
    }

    fun updateFilteredList(newList: List<T>) {
        list = newList as ArrayList<T> // Update filtered list
        notifyDataSetChanged() // Refresh the adapter with the filtered list
    }
}
