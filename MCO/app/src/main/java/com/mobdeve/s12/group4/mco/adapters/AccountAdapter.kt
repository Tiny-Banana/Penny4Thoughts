package com.mobdeve.s12.group4.mco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.Account
import com.mobdeve.s12.group4.mco.utility.BalanceCalculator
import java.text.DecimalFormat

class AccountAdapter(var accounts: ArrayList<Account>) : RecyclerView.Adapter<AccountAdapter.AccountsHolder>() {

    inner class AccountsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val accountName : TextView =  itemView.findViewById(R.id.accountName)
        val accountImg : ImageView = itemView.findViewById(R.id.accountImg)
        val accountBal : TextView = itemView.findViewById(R.id.accountBal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
        return AccountsHolder(view)
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    override fun onBindViewHolder(holder: AccountsHolder, position: Int) {
        val account = accounts[position]
        val decimalFormat = DecimalFormat("#,##0.00")

        holder.accountName.text = account.name
        holder.accountBal.text = decimalFormat.format(account.balance)
        holder.accountImg.setImageResource(account.imageId)
    }

    fun addAccount(account: Account) {
        accounts.add(account)
        notifyItemInserted(accounts.size - 1)
    }
}