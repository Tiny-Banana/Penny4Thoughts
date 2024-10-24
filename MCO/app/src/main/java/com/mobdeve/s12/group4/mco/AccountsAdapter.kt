package com.mobdeve.s12.group4.mco

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccountsAdapter(var accounts: List <Account>) : RecyclerView.Adapter<AccountsAdapter.AccountsHolder>() {

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
        holder.accountName.text = account.name
        holder.accountBal.text = account.balance.toString()
        holder.accountImg.setImageResource(account.imageId)
    }
}