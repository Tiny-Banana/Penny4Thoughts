package com.mobdeve.s12.group4.mco.utility

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.adapters.AccountAdapter
import com.mobdeve.s12.group4.mco.adapters.IconAdapter
import com.mobdeve.s12.group4.mco.adapters.ParentAdapter
import com.mobdeve.s12.group4.mco.adapters.SpinnerAdapter
import com.mobdeve.s12.group4.mco.fragments.AnalysisFragment
import com.mobdeve.s12.group4.mco.fragments.BudgetFragment
import com.mobdeve.s12.group4.mco.fragments.CategoryFragment
import com.mobdeve.s12.group4.mco.fragments.HomeFragment
import com.mobdeve.s12.group4.mco.fragments.RecordsFragment
import com.mobdeve.s12.group4.mco.models.Account
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.models.CustomDate
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction
import java.util.Calendar

class PopupManager() {
    fun showAccountPopup(activity: AppCompatActivity, accountAdapter: AccountAdapter, iconAdapter: IconAdapter, account: Account?, position: Int) {
        val popupView = activity.layoutInflater.inflate(R.layout.popup_new_account, null)
        val accAction = popupView.findViewById<TextView>(R.id.accountTitleAction)
        val accNameEditText = popupView.findViewById<EditText>(R.id.acc_name)
        val initialAmtEditText = popupView.findViewById<EditText>(R.id.acc_amt)
        val saveBtn = popupView.findViewById<MaterialButton>(R.id.trans_saveBtn)
        val cancelBtn = popupView.findViewById<MaterialButton>(R.id.trans_cancelBtn)
        val iconsRV = popupView.findViewById<RecyclerView>(R.id.acc_popup_rv)

        // Create the PopupWindow
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        popupWindow.isFocusable = true
        popupWindow.update()
        popupWindow.showAtLocation(activity.findViewById(R.id.flFragment), Gravity.CENTER, 0, 0)

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Apply dim effect to the background
        val window = activity.window
        val layoutParams = window.attributes
        layoutParams.alpha = 0.7f // Adjust the dim level (1.0 is fully bright, 0.0 is completely dimmed)
        window.attributes = layoutParams

        popupShow(popupView)

        // Pre-fill the fields if editing an existing account
        if (account != null) {
            accAction.text = "Edit account"
            accNameEditText.setText(account.name)
            initialAmtEditText.setText(account.balance.toString())
            iconAdapter.selectIcon(account.imageId)
        }

        cancelBtn.setOnClickListener {
            popupDismiss(popupView, popupWindow, window, layoutParams)
            iconAdapter.clearSelection()
        }

        saveBtn.setOnClickListener {
            val accName = accNameEditText.text.toString().trim()
            val initialAmt = initialAmtEditText.text.toString().trim()
            val selectedIcon = iconAdapter.getSelectedIcon()

            if (accName.isEmpty() || initialAmt.isEmpty() || selectedIcon == null) {
                Toast.makeText(activity, "Please fill all fields and select an icon.", Toast.LENGTH_SHORT).show()
            } else {
                if (account == null) {
                    // Adding a new account
                    val newAccount = Account(
                        accountAdapter.accounts.size + 1L,
                        selectedIcon.imageID,
                        accName,
                        initialAmt.toDouble(),
                        mutableListOf()
                    )
                    accountAdapter.addToAccounts(newAccount)
                } else {
                    // Editing an existing account
                    account.name = accName
                    account.balance = initialAmt.toDouble()
                    account.imageId = selectedIcon.imageID
                    accountAdapter.notifyItemChanged(position)
                }
            }

            popupDismiss(popupView, popupWindow, window, layoutParams)
            iconAdapter.clearSelection()
        }

        iconsRV.adapter = iconAdapter
        iconsRV.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    fun showTransactionPopup(
        activity: AppCompatActivity,
        homeFragment: HomeFragment,
        recordsFragment: RecordsFragment,
        categoryFragment: CategoryFragment,
        analysisFragment: AnalysisFragment,
        budgetFragment: BudgetFragment,
        accountAdapter: AccountAdapter,
        recordsAdapter: ParentAdapter<TransacParent, Transaction>,
        budgetAdapter: ParentAdapter<CategoryParent, Category>,
        accountSpinnerAdapter: SpinnerAdapter<Account>,
        filter: Filter,
        categories: ArrayList<Category>,
        transaction: Transaction?
    ) {
        val regular = Typeface.DEFAULT
        val bold = ResourcesCompat.getFont(activity, R.font.poppins_bold)
        val popupView = activity.layoutInflater.inflate(R.layout.popup_transaction, null)
        val saveBtn = popupView.findViewById<MaterialButton>(R.id.trans_saveBtn)
        val cancelBtn = popupView.findViewById<MaterialButton>(R.id.trans_cancelBtn)
        val accSpinner = popupView.findViewById<Spinner>(R.id.spin_acc)
        val categorySpinner = popupView.findViewById<Spinner>(R.id.spin_category)
        val incomeTxtView = popupView.findViewById<TextView>(R.id.trans_income)
        val expenseTxtView = popupView.findViewById<TextView>(R.id.trans_expense)
        val dateTxtView = popupView.findViewById<TextView>(R.id.trans_date)
        val notesEditText = popupView.findViewById<EditText>(R.id.trans_notes)
        val amountEditText = popupView.findViewById<EditText>(R.id.trans_amt)
        val action = popupView.findViewById<TextView>(R.id.transactionActionTitle)

        // Create the PopupWindow
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        popupWindow.isFocusable = true
        popupWindow.update()
        popupWindow.showAtLocation(activity.findViewById(R.id.flFragment), Gravity.CENTER, 0, 0)

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Apply dim effect to the background
        val window = activity.window
        val layoutParams = window.attributes
        layoutParams.alpha = 0.7f // Adjust the dim level (1.0 is fully bright, 0.0 is completely dimmed)
        window.attributes = layoutParams

        popupShow(popupView)

        accSpinner.adapter = accountSpinnerAdapter

        // Set initial category adapter
        val incomeCategorySpinnerAdapter: SpinnerAdapter<Category> = SpinnerAdapter(
            activity,
            ArrayList(categories.filter { it.type == "Income" }),
            { it.name },
            { it.imageId }
        )
        val expenseCategorySpinnerAdapter: SpinnerAdapter<Category> = SpinnerAdapter(
            activity,
            ArrayList(categories.filter { it.type == "Expense" }),
            { it.name },
            { it.imageId }
        )

        // Default Category
        incomeTxtView.typeface = bold
        categorySpinner.adapter = incomeCategorySpinnerAdapter

        // Default Date
        val today = CustomDate()
        dateTxtView.text = today.toStringFull()
        var selectedDate = today

        incomeTxtView.setOnClickListener {
            incomeTxtView.typeface = bold
            expenseTxtView.typeface = regular
            categorySpinner.adapter = incomeCategorySpinnerAdapter
        }

        expenseTxtView.setOnClickListener {
            expenseTxtView.typeface = bold
            incomeTxtView.typeface = regular
            categorySpinner.adapter = expenseCategorySpinnerAdapter
        }

        dateTxtView.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                activity, R.style.DialogTheme,
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = CustomDate(selectedYear, selectedMonth, selectedDay)
                    dateTxtView.text = selectedDate.toStringFull()
                },
                year, month, day
            )

            datePickerDialog.show()
        }

        // Pre-fill data if editing
        if (transaction != null) {
            // Populate fields with the current transaction's data
            if (transaction.type == "Expense") {
                expenseTxtView.typeface = bold
                incomeTxtView.typeface = regular
                categorySpinner.adapter = expenseCategorySpinnerAdapter
                categorySpinner.setSelection(expenseCategorySpinnerAdapter.returnList().indexOf(transaction.category))
            } else {
                incomeTxtView.typeface = bold
                expenseTxtView.typeface = regular
                categorySpinner.adapter = incomeCategorySpinnerAdapter
                categorySpinner.setSelection(incomeCategorySpinnerAdapter.returnList().indexOf(transaction.category))
            }

            accSpinner.setSelection(accountAdapter.accounts.indexOf(transaction.account))
            amountEditText.setText(transaction.amount.toString())
            notesEditText.setText(transaction.notes)
            dateTxtView.text = transaction.createdAt.toStringFull()
            action.text = "Edit transaction"
        }

        cancelBtn.setOnClickListener {
            popupDismiss(popupView, popupWindow, window, layoutParams)
        }

        saveBtn.setOnClickListener {
            // Get the inputs
            val selectedAccount = accSpinner.selectedItem as Account
            val selectedCategory = categorySpinner.selectedItem as Category
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val notes = notesEditText.text.toString()
            val transactionType = if (incomeTxtView.typeface == bold) "Income" else "Expense"

            // Validate inputs
            if (amount == null || amount <= 0) {
                Toast.makeText(activity, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transactionToUpdate = transaction ?: Transaction(
                id = recordsAdapter.originalList.size + 1L,
                amount = amount,
                type = transactionType,
                category = selectedCategory,
                account = selectedAccount,
                notes = notes,
                createdAt = selectedDate
            )

            if (transaction == null) {
                // Adding a new transaction
                recordsAdapter.addItem(transactionToUpdate)
                selectedAccount.addTransaction(transactionToUpdate)
                selectedCategory.addTransaction(transactionToUpdate)
            } else {
                // Editing an existing transaction
                with(transactionToUpdate) {
                    this.amount = amount
                    this.type = transactionType
                    this.category = selectedCategory
                    this.account = selectedAccount
                    this.notes = notes
                    this.createdAt = selectedDate
                }
                selectedAccount.updateTransaction(transactionToUpdate)
                selectedCategory.updateTransaction(transactionToUpdate)
                recordsAdapter.notifyDataSetChanged()
            }

            // Update account balance
            selectedAccount.balance = if (transactionToUpdate.type == "Expense") {
                transactionToUpdate.account.balance - transactionToUpdate.amount
            } else {
                transactionToUpdate.account.balance + transactionToUpdate.amount
            }

            // Notify account adapter
            accountAdapter.accounts.indexOfFirst { it.id == selectedAccount.id }
                .takeIf { it != -1 }
                ?.let { accountAdapter.notifyItemChanged(it) }

            // Update budget
            selectedCategory.getBudgetForMonth(filter.selectedMonthNum, filter.selectedYear)?.let { budget ->
                budget.spent += transactionToUpdate.amount
                budget.remaining -= transactionToUpdate.amount
                budgetAdapter.notifyDataSetChanged()
            }

            homeFragment.updateBalance()
            categoryFragment.updateBalance()
            recordsFragment.updateBalance()
            analysisFragment.updateBalance()
            analysisFragment.updateFilterPieChart()
            budgetFragment.updateBudget()

            // Notify the user and close the popup
            Toast.makeText(activity, "Transaction saved!", Toast.LENGTH_SHORT).show()

            popupDismiss(popupView, popupWindow, window, layoutParams)
        }
    }

    fun popupDismiss(popupView: View, popupWindow: PopupWindow, window: Window, layoutParams: WindowManager.LayoutParams) {
        popupView.animate()
            .translationY(1000f)  // Slide down off-screen
            .alpha(0f)            // Fade out
            .setDuration(400)     // Duration of animation
            .withEndAction {
                // Dismiss the popup after animation is complete
                popupWindow.dismiss()
            }
            .start()

        layoutParams.alpha = 1.0f
        window.attributes = layoutParams
    }

    fun popupShow(popupView: View) {
        // Set initial position off-screen
        popupView.translationY = 1000f  // Moves the popup down off-screen (adjust as needed)

        // Fade in and slide up animation
        popupView.animate()
            .translationY(0f)  // Slide up to its original position
            .alpha(1f)         // Fade in
            .setDuration(400)  // Duration of animation
            .start()
    }
}
