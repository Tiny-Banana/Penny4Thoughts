package com.mobdeve.s12.group4.mco.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent

class SpinnerAdapter<T : Any>(var context: Context,
                              var items: ArrayList<T>,
                              var getName: (T) -> String,
                              var getImageId: (T) -> Int   ) : BaseAdapter() {

    private lateinit var categoryAdapter: ParentAdapter<CategoryParent, Category>

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val item = items[position]

        viewHolder.name.text = getName(item)
        viewHolder.image.setImageResource(getImageId(item))

        return view
    }

    fun returnList(): ArrayList<T> {
        return items
    }

    fun filterCategory(type: String) {
        val categories = categoryAdapter.getChildList().filterIsInstance<Category>()
        val filteredCategories = categories.filter { it.type == type }
        items = ArrayList(filteredCategories as List<T>)
        notifyDataSetChanged()
    }

    fun setCategoryAdapter(categoryAdapter: ParentAdapter<CategoryParent, Category>) {
        this.categoryAdapter = categoryAdapter
        this.items = categoryAdapter.getChildList() as ArrayList<T>
    }

    fun updateItems() {
        this.items = categoryAdapter.getChildList() as ArrayList<T>
    }

    private class ViewHolder(view: View) {
        val name: TextView = view.findViewById(R.id.spin_name)
        val image: ImageView = view.findViewById(R.id.spin_img)
    }
}