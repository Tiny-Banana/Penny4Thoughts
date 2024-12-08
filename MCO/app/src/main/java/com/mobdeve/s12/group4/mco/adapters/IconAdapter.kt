package com.mobdeve.s12.group4.mco.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.IconItem

class IconAdapter(var icons: List<IconItem>) : RecyclerView.Adapter<IconAdapter.IconHolder>() {
    private var selectedPosition = -1

    inner class IconHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon_img : ImageView = itemView.findViewById(R.id.item_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false)
        return IconHolder(view)
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    override fun onBindViewHolder(holder: IconHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentItem = icons[position]
        holder.icon_img.setImageResource(currentItem.imageID)

        // Create a border drawable
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        borderDrawable.setStroke(5, Color.parseColor(currentItem.bgColor))  // 5px border width with green color

        // Optionally, set a corner radius (for rounded corners)
        borderDrawable.cornerRadius = 12f  // Adjust as needed

        // Highlight selected item with a border
        if (currentItem.isSelected) {
            holder.itemView.background = borderDrawable  // Apply border to selected item
        } else {
            // Remove the border (or set a transparent border if you prefer)
            holder.itemView.background = null
        }

        // Set click listener to toggle selection
        holder.itemView.setOnClickListener {
            if (selectedPosition != position) {
                if (selectedPosition != -1) {
                    icons[selectedPosition].isSelected = false
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = position
                currentItem.isSelected = true
                notifyItemChanged(position)
            }
        }
    }

    fun getSelectedIcon(): IconItem? {
        return if (selectedPosition != -1) icons[selectedPosition] else null
    }

    fun selectIcon(imageID: Int) {
        val position = icons.indexOfFirst { it.imageID == imageID }
        if (position != -1) {
            if (selectedPosition != -1) {
                icons[selectedPosition].isSelected = false
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = position
            icons[position].isSelected = true
            notifyItemChanged(position)
        }
    }

    fun clearSelection() {
        if (selectedPosition != -1) {
            icons[selectedPosition].isSelected = false
            notifyItemChanged(selectedPosition)
            selectedPosition = -1
        }
    }
}