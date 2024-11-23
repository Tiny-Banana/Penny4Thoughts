package com.mobdeve.s12.group4.mco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.Category

class CategoryChildAdapter(val list: List<Category>) : RecyclerView.Adapter<CategoryChildAdapter.CategoryHolder>() {
    inner class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt : TextView = itemView.findViewById(R.id.child_cat_text)
        val img : ImageView = itemView.findViewById(R.id.child_cat_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_category_child, parent, false)
        return CategoryHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val item = list[position]
        holder.txt.text = item.name
        holder.img.setImageResource(item.imageId)
    }
}