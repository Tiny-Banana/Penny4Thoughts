package com.mobdeve.s12.group4.mco.utility

import android.app.DatePickerDialog
import android.graphics.Typeface
import android.view.Gravity
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
import com.mobdeve.s12.group4.mco.fragments.CategoryFragment
import com.mobdeve.s12.group4.mco.fragments.HomeFragment
import com.mobdeve.s12.group4.mco.fragments.RecordsFragment
import com.mobdeve.s12.group4.mco.models.Account
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CustomDate
import com.mobdeve.s12.group4.mco.models.TransacParent
import com.mobdeve.s12.group4.mco.models.Transaction
import java.util.Calendar

class PopupManager(
    private val activity: AppCompatActivity,
    private val homeFragment: HomeFragment,
    private val recordsFragment: RecordsFragment,
    private val categoryFragment: CategoryFragment,
    private val accountAdapter: AccountAdapter,
    private val recordsAdapter: ParentAdapter<TransacParent, Transaction>,
    private val accountSpinnerAdapter: SpinnerAdapter<Account>,
    private val iconAdapter: IconAdapter,
    private val categories: ArrayList<Category>,
) {

    fun showAddAcc() {
        val popupView = activity.layoutInflater.inflate(R.layout.popup_new_account, null)
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

        cancelBtn.setOnClickListener {
            popupWindow.dismiss()
            iconAdapter.clearSelection()
        }
        saveBtn.setOnClickListener {
            val accName = accNameEditText.text.toString().trim()
            val initialAmt = initialAmtEditText.text.toString().trim()
            val selectedIcon = iconAdapter.getSelectedIcon()

            if (accName.isEmpty() || initialAmt.isEmpty() || selectedIcon == null) {
                Toast.makeText(activity, "Please fill all fields and select an icon.", Toast.LENGTH_SHORT).show()
            } else {
                val newAccount = Account(accountAdapter.accounts.size + 1L,
                                        selectedIcon.imageID,
                                        accName,
                                        initialAmt.toDouble(),
                                        mutableListOf())
                accountAdapter.addAccount(newAccount)
            }

            popupWindow.dismiss()
            iconAdapter.clearSelection()
        }

        iconsRV.adapter = iconAdapter
        iconsRV.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    fun showAddTransaction() {
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

        // Create the PopupWindow
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        popupWindow.isFocusable = true
        popupWindow.update()
        popupWindow.showAtLocation(activity.findViewById(R.id.flFragment), Gravity.CENTER, 0, 0)

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

        cancelBtn.setOnClickListener {
            popupWindow.dismiss()
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

            // Create the Transaction object
            val transaction = Transaction(
                recordsAdapter.originalList.size + 1L,
                amount,
                transactionType,
                selectedCategory,
                selectedAccount,
                notes,
                selectedDate
            )

            recordsAdapter.addItem(transaction)

            val account = transaction.account
            val category = transaction.category

            account.addTransaction(transaction)
            category.addTransaction(transaction)

            if (transaction.type == "Expense") {
                account.setBalance(transaction.account.balance - transaction.amount)
            } else {
                account.setBalance(transaction.account.balance + transaction.amount)
            }

            val accountPosition = accountAdapter.accounts.indexOfFirst { it.id == account.id }
            accountAdapter.notifyItemChanged(accountPosition)

            homeFragment.updateBalance()
            categoryFragment.updateBalance()
            recordsFragment.updateBalance()

            // Notify the user and close the popup
            Toast.makeText(activity, "Transaction saved!", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }
}
