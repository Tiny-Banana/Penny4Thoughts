package com.mobdeve.s12.group4.mco

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mobdeve.s12.group4.mco.adapters.AccountAdapter
import com.mobdeve.s12.group4.mco.adapters.IconAdapter
import com.mobdeve.s12.group4.mco.models.Account

class PopupManager(
    private val activity: AppCompatActivity,
    private val accountAdapter: AccountAdapter,
    private val iconAdapter: IconAdapter
) {

    fun showNewAccPopUp() {
        val popupView = activity.layoutInflater.inflate(R.layout.popup_new_account, null)
        val accNameEditText = popupView.findViewById<TextView>(R.id.acc_name)
        val initialAmtEditText = popupView.findViewById<TextView>(R.id.acc_amt)
        val saveBtn = popupView.findViewById<MaterialButton>(R.id.saveBtn)
        val cancelBtn = popupView.findViewById<MaterialButton>(R.id.cancelBtn)
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
                val newAccount = Account(selectedIcon.imageID, accName, initialAmt.toFloat(), mutableListOf())
                accountAdapter.addAccount(newAccount)
            }

            popupWindow.dismiss()
            iconAdapter.clearSelection()
        }

        iconsRV.adapter = iconAdapter
        iconsRV.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }
}
