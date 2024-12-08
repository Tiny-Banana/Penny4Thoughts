package com.mobdeve.s12.group4.mco.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.Account
import com.mobdeve.s12.group4.mco.utility.PopupManager
import java.text.DecimalFormat

class AccountAdapter(
    private val activity: AppCompatActivity,
    var accounts: ArrayList<Account>,
    private val iconAdapter: IconAdapter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ACCOUNT = 0
    private val VIEW_TYPE_BUTTON = 1
    private val popupManager = PopupManager()

    inner class AccountsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val accountName: TextView = itemView.findViewById(R.id.accountName)
        val accountImg: ImageView = itemView.findViewById(R.id.accountImg)
        val accountBal: TextView = itemView.findViewById(R.id.accountBal)
        val accountMore: ImageView = itemView.findViewById(R.id.accountMore)
    }

    inner class ButtonHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addButton: MaterialButton = itemView.findViewById(R.id.addAccount)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < accounts.size) VIEW_TYPE_ACCOUNT else VIEW_TYPE_BUTTON
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ACCOUNT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
            AccountsHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.btn_add_newacc, parent, false)
            ButtonHolder(view)
        }
    }

    override fun getItemCount(): Int {
        // Account items + 1 for the button
        return accounts.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AccountsHolder) {
            val account = accounts[position]
            val decimalFormat = DecimalFormat("#,##0.00")

            holder.accountName.text = account.name
            holder.accountBal.text = decimalFormat.format(account.balance)
            holder.accountImg.setImageResource(account.imageId)
            holder.accountMore.setOnClickListener {
                showMoreAccountPopup(account, holder)
            }
        } else if (holder is ButtonHolder) {
            holder.addButton.setOnClickListener {
                addAcount()
            }
        }
    }

    fun addToAccounts(account: Account) {
        accounts.add(account)
        notifyItemInserted(accounts.size - 1)
    }

    private fun showMoreAccountPopup(account: Account, holder: AccountsHolder) {
        val popupMenu = PopupMenu(holder.itemView.context, holder.accountMore)

        // Inflate the menu resource into the popup menu
        popupMenu.menuInflater.inflate(R.menu.popup_more, popupMenu.menu)
        popupMenu.gravity = Gravity.END

        //Set click listeners for the popup menu items
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    editAccount(account)
                    true
                }
                R.id.delete -> {
                    deleteAccount(account)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun addAcount() {
        popupManager.showAccountPopup(activity, this, iconAdapter, null, 0)
    }

    private fun editAccount(account: Account) {
        val position = this.accounts.indexOfFirst { it.id == account.id }

        if (position != -1) {
            popupManager.showAccountPopup(activity, this, iconAdapter, account, position)
        }
    }

    private fun deleteAccount(account: Account) {
        val position = this.accounts.indexOfFirst { it.id == account.id }

        if (position != -1) {
            // Position found, proceed with editing logic
            accounts.remove(account)
            notifyItemRemoved(position)
        }
    }
}