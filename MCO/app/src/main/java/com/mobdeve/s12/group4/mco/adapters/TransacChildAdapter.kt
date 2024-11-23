package com.mobdeve.s12.group4.mco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.Transaction
import java.text.DecimalFormat

class TransacChildAdapter(val list: List<Transaction>) : RecyclerView.Adapter<TransacChildAdapter.TransacHolder>() {

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
    }
}