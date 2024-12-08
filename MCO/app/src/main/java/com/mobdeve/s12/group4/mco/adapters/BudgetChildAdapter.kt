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
            }
            is NotBudgetedHolder -> {
                holder.catNBudgetedName.text = item.name
                holder.catNBudgetedImg.setImageResource(item.imageId)
                holder.catNBudgetedBtn.setOnClickListener{
                    showAddBudgetPopup(item, holder)
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

    private fun showAddBudgetPopup(item: Category, holder: RecyclerView.ViewHolder) {
        val popupView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.popup_budget, null)
        val popupBudgetImg = popupView.findViewById<ImageView>(R.id.popupBudgetImg)
        val popupBudgetName = popupView.findViewById<TextView>(R.id.popupBudgetName)
        val popupBudgetMonth = popupView.findViewById<TextView>(R.id.popupBudgetMonth)
        val popupBudgetYear = popupView.findViewById<TextView>(R.id.popupBudgetYear)
        val saveBtn = popupView.findViewById<MaterialButton>(R.id.budgetSave)
        val cancelBtn = popupView.findViewById<MaterialButton>(R.id.budgetCancel)
        val limitEditTxt = popupView.findViewById<EditText>(R.id.budget_limit)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        popupBudgetImg.setImageResource(item.imageId)
        popupBudgetName.text = item.name
        popupBudgetMonth.text = filter.selectedMonth
        popupBudgetYear.text = filter.selectedYear.toString()

        popupWindow.isFocusable = true
        popupWindow.update()
        val rootView = holder.itemView.rootView // Access the root view of the item
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0)

        cancelBtn.setOnClickListener {
            popupWindow.dismiss()
        }

        saveBtn.setOnClickListener {
            val limit = limitEditTxt.text.toString().trim()
            if (limit.isEmpty()) {
                Toast.makeText(holder.itemView.context, "Please fill all fields and select an icon.", Toast.LENGTH_SHORT).show()
            } else {
                val newBudget = Budget(limit.toDouble(), 0.0, limit.toDouble(), filter.selectedMonthNum, filter.selectedYear)
                item.addBudget(newBudget)
                filter.filterCategoryBudget(filter.selectedMonthNum, filter.selectedYear, parentAdapter)
                budgetFragment.updateBudget()
            }

            popupWindow.dismiss()
        }
    }
}