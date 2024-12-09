package com.mobdeve.s12.group4.mco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction
import com.mobdeve.s12.group4.mco.utility.Filter
import com.mobdeve.s12.group4.mco.utility.PopupManager

class ParentAdapter<T : Any, U : Any>(
    var list: ArrayList<T>,
    private val getSection: (T) -> String,
    private val getChildList: (T) -> List<U>,
    private val childAdapterFactory: (List<U>) -> RecyclerView.Adapter<*>,
    private val filter: Filter,
    private val activity: AppCompatActivity,
    private val iconAdapter: IconAdapter,
    private val categorySpinnerAdapter: SpinnerAdapter<Category>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LIST = 0
    private val VIEW_TYPE_BUTTON = 1
    private val popupManager = PopupManager()
    var originalList = ArrayList<T>(list) // Store the original list

    inner class ButtonHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addButton: MaterialButton = itemView.findViewById(R.id.addAccount)
    }

    inner class ParentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val section: TextView = itemView.findViewById(R.id.section)
        val childRV: RecyclerView = itemView.findViewById(R.id.child_recycler_view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < originalList.size) VIEW_TYPE_LIST else VIEW_TYPE_BUTTON
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_LIST) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_parent, parent, false)
            return ParentHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.btn_add, parent, false)
            ButtonHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ParentAdapter<*, *>.ParentHolder) {
            val item = list[position]  // Bind to the filtered list
            holder.section.text = getSection(item)
            holder.childRV.apply {
                adapter = childAdapterFactory(getChildList(item))
            }
        } else if (holder is ParentAdapter<*, *>.ButtonHolder) {
            holder.addButton.text = "Add Category"
            holder.addButton.setOnClickListener {
                popupManager.showCategoryPopup(activity,
                    this as ParentAdapter<CategoryParent, Category>,
                    iconAdapter, null, filter)
            }
        }

    }

    override fun getItemCount(): Int {
        return if (childAdapterFactory(getChildList(list.firstOrNull() ?: return 0)) is CategoryChildAdapter) {
            list.size + 1 // Add 1 if the child adapter is CategoryChildAdapter
        } else {
            list.size // Default case
        }
    }

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
                filter.applyFilter(filter.selectedMonth,
                    filter.selectedYear,
                    parentList,
                    this as ParentAdapter<TransacParent, Transaction>)
            }

            is Category -> {
                val parentList = originalList as ArrayList<CategoryParent>
                val section = item.type + " Categories"
                val existingParent = parentList.find { it.section == section }

                if (existingParent != null) {
                    // Add the transaction to the existing parent's child list
                    existingParent.list.add(item)
                } else {
                    // Create a new parent and add it to the list
                    val newParent = CategoryParent(section, arrayListOf(item))
                    parentList.add(newParent)
                }
            }
        }
    }

    fun editItem(item: U) {
        when(item) {
            is Transaction -> {
                val parentList = originalList as ArrayList<TransacParent>

                val section = item.createdAt.toStringFull()

                val existingParent = parentList.find { it.section == section }

                // Remove the item from its old section (if it exists)
                val oldParent = parentList.find { it.list.contains(item) }
                oldParent?.list?.remove(item)

                if (existingParent != null) {
                    // Add the transaction to the existing parent's child list
                    existingParent.list.add(item)
                } else {
                    // Create a new parent and add it to the list
                    val newParent = TransacParent(section, arrayListOf(item))
                    parentList.add(newParent)
                }

                // Pass the entire parent list
                filter.applyFilter(filter.selectedMonth,
                    filter.selectedYear,
                    parentList,
                    this as ParentAdapter<TransacParent, Transaction>)
            }

            is Category -> {
                val parentList = originalList as ArrayList<CategoryParent>
                val section = item.type + " Categories"
                val existingParent = parentList.find { it.section == section }

                // Remove the item from its old section (if it exists)
                val oldParent = parentList.find { it.list.contains(item) }
                oldParent?.list?.remove(item)

                if (existingParent != null) {
                    // Add the transaction to the existing parent's child list
                    existingParent.list.add(item)
                } else {
                    // Create a new parent and add it to the list
                    val newParent = CategoryParent(section, arrayListOf(item))
                    parentList.add(newParent)
                }
            }
        }
    }

    fun updateFilteredList(newList: List<T>) {
        list = newList as ArrayList<T> // Update filtered list
        categorySpinnerAdapter.updateItems()
        removeEmptySection()
        notifyDataSetChanged() // Refresh the adapter with the filtered list
    }

    private fun removeEmptySection() {
        list.removeIf { parent ->
            val childList = when (parent) {
                is CategoryParent -> parent.list
                is TransacParent -> parent.list
                else -> null // Handle unexpected parent types
            }
            childList?.isEmpty() == true // Remove parent if child list is empty
        }
    }

    fun getChildList(): List<Any> {
        return originalList.flatMap {
            when (it) {
                is TransacParent -> it.list
                is CategoryParent -> it.list
                else -> emptyList()
            }
        }
    }
}
