package com.mobdeve.s12.group4.mco.adapters

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.fragments.AnalysisFragment
import com.mobdeve.s12.group4.mco.fragments.BudgetFragment
import com.mobdeve.s12.group4.mco.fragments.CategoryFragment
import com.mobdeve.s12.group4.mco.fragments.HomeFragment
import com.mobdeve.s12.group4.mco.fragments.RecordsFragment
import com.mobdeve.s12.group4.mco.models.Account
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction
import com.mobdeve.s12.group4.mco.utility.Filter
import com.mobdeve.s12.group4.mco.utility.PopupManager
import java.text.DecimalFormat

class TransacChildAdapter(
    private val parentAdapter: ParentAdapter<TransacParent, Transaction>,
    val list: List<Transaction>,
    val filter: Filter,
    private val activity: AppCompatActivity,
    private val homeFragment: HomeFragment,
    private val recordsFragment: RecordsFragment,
    private val categoryFragment: CategoryFragment,
    private val analysisFragment: AnalysisFragment,
    private val budgetFragment: BudgetFragment,
    private val accountAdapter: AccountAdapter,
    private val recordsAdapter: ParentAdapter<TransacParent, Transaction>,
    private val budgetAdapter: ParentAdapter<CategoryParent, Category>,
    private val accountSpinnerAdapter: SpinnerAdapter<Account>,
    private val categories: ArrayList<Category>):

    RecyclerView.Adapter<TransacChildAdapter.TransacHolder>(){

    private lateinit var recordFragment: RecordsFragment
    private val popupManager = PopupManager()

    inner class TransacHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catName : TextView = itemView.findViewById(R.id.trans_category)
        val catImg : ImageView = itemView.findViewById(R.id.trans_img)
        val accName : TextView = itemView.findViewById(R.id.trans_acc)
        val accImg : ImageView = itemView.findViewById(R.id.trans_acc_img)
        val amt : TextView = itemView.findViewById(R.id.trans_rec_amt)
        val sign : TextView = itemView.findViewById(R.id.trans_sign)
        val peso : TextView = itemView.findViewById(R.id.trans_peso)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_transac_child, parent, false)
        return TransacHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TransacHolder, position: Int) {
        val item = list[position]

        holder.catName.text = item.category.name
        holder.catImg.setImageResource(item.category.imageId)
        holder.accName.text = item.account.name
        holder.accImg.setImageResource(item.account.imageId)

        val decimalFormat = DecimalFormat("#,##0.00")
        val formattedAmount = decimalFormat.format(item.amount)
        holder.amt.text = formattedAmount

        if (item.type == "Income") {
            holder.amt.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
            holder.sign.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
            holder.peso.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
            holder.sign.text = "+"
        }

        holder.itemView.setOnClickListener {
            showRecordPopup(item, holder)
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        // Find the TransacParent that contains the transaction you want to delete
        val transacParent = parentAdapter.originalList.find { parent -> parent.list.contains(transaction) }

        // If the TransacParent is found, remove the transaction
        transacParent?.list?.remove(transaction)

        filter.applyFilter(filter.selectedMonth,
            filter.selectedYear,
            parentAdapter.originalList,
            parentAdapter)

        recordsFragment.listEmpty()
        recordFragment.updateBalance()
    }

    private fun editTransaction(transaction: Transaction) {
        popupManager.showTransactionPopup(
            activity,
            homeFragment,
            recordsFragment,
            categoryFragment,
            analysisFragment,
            budgetFragment,
            accountAdapter,
            recordsAdapter,
            budgetAdapter,
            accountSpinnerAdapter,
            filter,
            categories,
            transaction
        )

//        val parentIndex = recordsAdapter.originalList.indexOfFirst { parent ->
//            // We need to find the child list (e.g., transactions) and check if it contains the updated item
//            val childList = getChildList(parent) // This gets the child list for the parent
//            childList.any { it == transaction }  // Compare the updated item with the child elements
//        }

        recordFragment.updateBalance()
    }

    private fun showRecordPopup(transaction: Transaction, holder: TransacHolder) {
        val popupView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.popup_record, null)
        val recordAccImg = popupView.findViewById<ImageView>(R.id.recordAccImg)
        val recordAccName = popupView.findViewById<TextView>(R.id.recordAccName)
        val recordCatImg = popupView.findViewById<ImageView>(R.id.recordCatImg)
        val recordCatName = popupView.findViewById<TextView>(R.id.recordCatName)
        val recordNote = popupView.findViewById<TextView>(R.id.recordNote)
        val recordAmt = popupView.findViewById<TextView>(R.id.recordAmt)
        val recordMonth = popupView.findViewById<TextView>(R.id.recordMonth)
        val recordDay = popupView.findViewById<TextView>(R.id.recordDay)
        val recordYr = popupView.findViewById<TextView>(R.id.recordYr)
        val bg = popupView.findViewById<View>(R.id.rectangleHolder1)
        val close = popupView.findViewById<ImageView>(R.id.recordClose)
        val edit = popupView.findViewById<ImageView>(R.id.recordEdit)
        val delete = popupView.findViewById<ImageView>(R.id.recordDelete)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val decimalFormat = DecimalFormat("#,##0.00")

        recordAccImg.setImageResource(transaction.account.imageId)
        recordAccName.text = transaction.account.name
        recordCatImg.setImageResource(transaction.category.imageId)
        recordCatName.text = transaction.category.name
        recordNote.text = transaction.notes
        recordAmt.text = decimalFormat.format(transaction.amount)
        recordMonth.text = transaction.createdAt.toStringMonth()
        recordYr.text = transaction.createdAt.year.toString()
        recordDay.text = transaction.createdAt.day_in_month.toString()

        if (transaction.type == "Expense") {
            bg.setBackgroundResource(R.drawable.rounded_rectangle_red)
        } else {
            bg.setBackgroundResource(R.drawable.rounded_rectangle_green)
        }

        popupWindow.isFocusable = true
        popupWindow.update()
        popupWindow.showAtLocation(holder.itemView.rootView, Gravity.CENTER, 0, 0)

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Apply dim effect to the background
        val window = (holder.itemView.context as Activity).window
        val layoutParams = window.attributes
        layoutParams.alpha = 0.7f // Adjust the dim level (1.0 is fully bright, 0.0 is completely dimmed)
        window.attributes = layoutParams

        close.setOnClickListener {
            popupManager.popupDismiss(popupView, popupWindow, window, layoutParams)
        }

        delete.setOnClickListener {
            deleteTransaction(transaction)
            popupManager.popupDismiss(popupView, popupWindow, window, layoutParams)
        }

        edit.setOnClickListener {
            editTransaction(transaction)
            popupManager.popupDismiss(popupView, popupWindow, window, layoutParams)
            layoutParams.alpha = 0.7f
            window.attributes = layoutParams
        }
    }

    fun setFragment(recordFragment: RecordsFragment) {
        this.recordFragment = recordFragment
    }
}