package com.mobdeve.s12.group4.mco.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mobdeve.s12.group4.mco.R

class SpinnerAdapter<T : Any>(var context: Context, var items: ArrayList<T>,
                              var getName: (T) -> String,
                              var getImageId: (T) -> Int   ) : BaseAdapter() {
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

        // Check if convertView is null (no recycled view available)
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder // Store the ViewHolder with the view for reuse
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder // Retrieve the cached ViewHolder
        }

        // Bind the data to the view
        val item = items[position]

        // Use the lambdas to retrieve name and image
        viewHolder.name.text = getName(item)
        viewHolder.image.setImageResource(getImageId(item))

        return view
    }

    // ViewHolder class for caching views
    private class ViewHolder(view: View) {
        val name: TextView = view.findViewById(R.id.spin_name)
        val image: ImageView = view.findViewById(R.id.spin_img)
    }
}