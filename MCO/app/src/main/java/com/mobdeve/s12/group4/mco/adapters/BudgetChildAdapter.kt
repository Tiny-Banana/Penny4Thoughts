package com.mobdeve.s12.group4.mco.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.fragments.BudgetFragment
import com.mobdeve.s12.group4.mco.models.Budget
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.utility.Filter
import java.text.DecimalFormat

class BudgetChildAdapter(
    private var parentAdapter: ParentAdapter<CategoryParent, Category>,
    var list: List<Category>,
    val filter: Filter): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var budgetFragment: BudgetFragment
    private var filteredCategories = list.filter { it.type == "Expense" }

    inner class BudgetedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catBudgetedImg: ImageView = itemView.findViewById(R.id.catBudgetedImg)
        val catBudgetedName: TextView = itemView.findViewById(R.id.catBudgetedName)
        val catBudgetedLimit: TextView = itemView.findViewById(R.id.catBudgetedLimit)
        val catBudgetedSpent: TextView = itemView.findViewById(R.id.catBudgetedSpent)
        val catBudgetedRem: TextView = itemView.findViewById(R.id.catBudgetedRem)
        val budgetedMore: ImageView = itemView.findViewById(R.id.budgetedMore)
    }

    inner class NotBudgetedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catNBudgetedImg: ImageView = itemView.findViewById(R.id.catNBudgetedImg)
        val catNBudgetedName: TextView = itemView.findViewById(R.id.catNBudgetedName)
        val catNBudgetedBtn: TextView = itemView.findViewById(R.id.catNBudgetedBtn)
    }

    override fun getItemViewType(position: Int): Int {
        val item = filteredCategories[position]
        val currentMonth = filter.selectedMonthNum
        val currentYear = filter.selectedYear

        return if (item.getBudgetForMonth(currentMonth, currentYear) != null) {
            1 // Budgeted layout
        } else {
            2 // Non-budgeted layout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> {
                val view = inflater.inflate(R.layout.item_budgeted, parent, false)
                BudgetedHolder(view)
            }
            2 -> {
                val view = inflater.inflate(R.layout.item_not_budgeted, parent, false)
                NotBudgetedHolder(view)
            }
            else -> throw IllegalArgumentException("Unsupported view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filteredCategories[position]
        val currentMonth = filter.selectedMonthNum
        val currentYear = filter.selectedYear
        val decimalFormat = DecimalFormat("#,##0.00")

        when (holder) {
            is BudgetedHolder -> {
                holder.catBudgetedName.text = item.name
                holder.catBudgetedImg.setImageResource(item.imageId)
                holder.catBudgetedLimit.text = decimalFormat.format(item.getBudgetForMonth(currentMonth, currentYear)?.limit)
                holder.catBudgetedRem.text = decimalFormat.format(item.getBudgetForMonth(currentMonth, currentYear)?.remaining)
                holder.catBudgetedSpent.text = decimalFormat.format(item.getBudgetForMonth(currentMonth, currentYear)?.spent)
                holder.budgetedMore.setOnClickListener{
                    showMoreBudgetPopup(item, holder)
                }
            }
            is NotBudgetedHolder -> {
                holder.catNBudgetedName.text = item.name
                holder.catNBudgetedImg.setImageResource(item.imageId)
                holder.catNBudgetedBtn.setOnClickListener{
                    addBudget(item, holder)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredCategories.size
    }

    fun setFragment(budgetFragment: BudgetFragment) {
        this.budgetFragment = budgetFragment
    }

    private fun showMoreBudgetPopup(category: Category, budgetHolder: BudgetedHolder) {
        val popupMenu = PopupMenu(budgetHolder.itemView.context, budgetHolder.budgetedMore)

        // Inflate the menu resource into the popup menu
        popupMenu.menuInflater.inflate(R.menu.popup_more, popupMenu.menu)
        popupMenu.gravity = Gravity.END

        //Set click listeners for the popup menu items
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    editBudget(category, budgetHolder)
                    true
                }
                R.id.delete -> {
                    deleteBudget(category)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun addBudget(category: Category, holder: RecyclerView.ViewHolder) {
        showBudgetPopup(category, holder, null)
    }

    private fun editBudget(category: Category, holder: RecyclerView.ViewHolder) {
        var budget = category.getBudgetForMonth(filter.selectedMonthNum, filter.selectedYear)

        if (budget != null) {
            showBudgetPopup(category, holder, budget)
        }
    }

    private fun deleteBudget(category: Category) {
        var budget = category.getBudgetForMonth(filter.selectedMonthNum, filter.selectedYear)

        if (budget != null) {
            category.removeBudget(budget)
        }

        filter.filterCategoryBudget(filter.selectedMonthNum, filter.selectedYear, parentAdapter)
        budgetFragment.updateBudget()
    }

    private fun showBudgetPopup(category: Category, holder: RecyclerView.ViewHolder, existingBudget: Budget?) {
        val popupView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.popup_budget, null)
        val popupBudgetImg = popupView.findViewById<ImageView>(R.id.popupBudgetImg)
        val popupBudgetName = popupView.findViewById<TextView>(R.id.popupBudgetName)
        val popupBudgetMonth = popupView.findViewById<TextView>(R.id.popupBudgetMonth)
        val popupBudgetYear = popupView.findViewById<TextView>(R.id.popupBudgetYear)
        val budgetAction = popupView.findViewById<TextView>(R.id.budgetActionTitle)
        val saveBtn = popupView.findViewById<MaterialButton>(R.id.budgetSave)
        val cancelBtn = popupView.findViewById<MaterialButton>(R.id.budgetCancel)
        val limitEditTxt = popupView.findViewById<EditText>(R.id.budget_limit)

        val popupWindow = PopupWindow(
                popupView,
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
        )

        popupBudgetImg.setImageResource(category.imageId)
        popupBudgetName.text = category.name
        popupBudgetMonth.text = filter.selectedMonth
        popupBudgetYear.text = filter.selectedYear.toString()

        // If there's an existing budget, populate the fields for editing
        if (existingBudget != null) {
            limitEditTxt.setText(existingBudget.limit.toString())
            budgetAction.text = "Edit Budget"
        }

        popupWindow.isFocusable = true
        popupWindow.update()
        popupWindow.showAtLocation(holder.itemView.rootView, Gravity.CENTER, 0, 0)

        cancelBtn.setOnClickListener {
            popupWindow.dismiss()
        }

        saveBtn.setOnClickListener {
            val limit = limitEditTxt.text.toString().trim()
            if (limit.isEmpty()) {
                Toast.makeText(holder.itemView.context, "Please fill all fields and select an icon.", Toast.LENGTH_SHORT).show()
            } else {
                if (existingBudget == null) {
                    // If it's a new budget
                    val newBudget = Budget(limit.toDouble(), 0.0, limit.toDouble(), filter.selectedMonthNum, filter.selectedYear)
                    category.addBudget(newBudget)
                } else {
                    existingBudget.limit = limit.toDouble()
                    existingBudget.remaining = existingBudget.limit - existingBudget.spent
                }
                filter.filterCategoryBudget(filter.selectedMonthNum, filter.selectedYear, parentAdapter)
                budgetFragment.updateBudget()
                popupWindow.dismiss()
            }
        }

    }
}