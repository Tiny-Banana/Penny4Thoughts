package com.mobdeve.s12.group4.mco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.Transaction
import java.text.DecimalFormat

class AnalysisAdapter(var categories: ArrayList<Category>):  RecyclerView.Adapter<AnalysisAdapter.AnalysisHolder>() {

    var filteredTransactions = emptyList<Transaction>()
        set(value) {
            field = value
            filterCategories() // Refilter categories when transactions change
        }

    var selectedType = "Income"
        set(value) {
            field = value
            filterCategories() // Refilter categories when the selected type changes
        }

    var filteredDateTransactions = emptyList<Transaction>()

    private var filteredCategories = categories

    inner class AnalysisHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catImg: ImageView =  itemView.findViewById(R.id.cat_img)
        val catName : TextView = itemView.findViewById(R.id.cat_name)
        val catAmt : TextView = itemView.findViewById(R.id.cat_amt)
        val catSign : TextView = itemView.findViewById(R.id.cat_sign)
        val catPeso : TextView = itemView.findViewById(R.id.cat_peso)
        val catPercent : TextView = itemView.findViewById(R.id.cat_percent)
        val catBar: ProgressBar = itemView.findViewById(R.id.cat_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_analyis, parent, false)
        return AnalysisHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredCategories.size
    }

    override fun onBindViewHolder(holder: AnalysisHolder, position: Int) {
        val category = filteredCategories[position]
        val decimalFormat = DecimalFormat("#,##0.00")
        val catTotal = aggregateTransactionsByCategory(filteredTransactions)
        val totalAmount = catTotal.values.sum()
        val catTotalPercent = calculateCategoryPercentages(catTotal, totalAmount)

        holder.catName.text = category.name
        holder.catImg.setImageResource(category.imageId)

        // Set category amount
        val categoryAmount = catTotal[category.name] ?: 0.0
        holder.catAmt.text = decimalFormat.format(categoryAmount)

        if (category.type == "Income") {
            holder.catAmt.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
            holder.catSign.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
            holder.catPeso.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
            holder.catSign.text = "+"
        } else {
            holder.catAmt.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_red))
            holder.catSign.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_red))
            holder.catPeso.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_red))
            holder.catSign.text = "-"
        }

        // Set category percentage text
        val percentage = catTotalPercent[category.name] ?: 0.0
        holder.catPercent.text = "${decimalFormat.format(percentage)} %"

        // Update the ProgressBar based on the percentage
        holder.catBar.max = 100 // Set the max value for the ProgressBar
        holder.catBar.progress = percentage.toInt() // Set progress as integer
    }

    private fun filterCategories() {
        val catTotal = aggregateTransactionsByCategory(filteredTransactions)

        filteredCategories = categories.filter { category ->
            val categoryAmount = catTotal[category.name] ?: 0.0
            categoryAmount != 0.0 && category.type == selectedType
        } as ArrayList<Category>

        filteredCategories.sortByDescending { category ->
            catTotal[category.name] ?: 0.0
        }

        notifyDataSetChanged() // Notify adapter about data changes
    }

    fun aggregateTransactionsByCategory(transactions: List<Transaction>): Map<String, Double> {
        return transactions.groupBy { it.category.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    fun calculateCategoryPercentages(categoryTotals: Map<String, Double>, totalAmount: Double): Map<String, Double> {
        return categoryTotals.mapValues { (_, total) ->
            if (totalAmount != 0.0) (total / totalAmount) * 100 else 0.0
        }
    }
}