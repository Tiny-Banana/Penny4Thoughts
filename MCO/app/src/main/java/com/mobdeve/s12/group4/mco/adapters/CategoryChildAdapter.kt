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
import com.mobdeve.s12.group4.mco.R
import com.mobdeve.s12.group4.mco.models.Category
import com.mobdeve.s12.group4.mco.models.CategoryParent
import com.mobdeve.s12.group4.mco.utility.Filter
import com.mobdeve.s12.group4.mco.utility.PopupManager

class CategoryChildAdapter(
    val list: List<Category>,
    private val activity: AppCompatActivity,
    private val categoryAdapter: ParentAdapter<CategoryParent, Category>,
    private val iconAdapter: IconAdapter,
    private val filter: Filter
) : RecyclerView.Adapter<CategoryChildAdapter.CategoryHolder>() {

    private val popupManager = PopupManager()
    inner class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt : TextView = itemView.findViewById(R.id.child_cat_text)
        val img : ImageView = itemView.findViewById(R.id.child_cat_img)
        val categoryMore: ImageView = itemView.findViewById(R.id.categoryMore)
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
        holder.categoryMore.setOnClickListener {
            showMoreCategoryPopup(item,holder)
        }
    }

    private fun editCategory(category: Category) {
        popupManager.showCategoryPopup(activity, categoryAdapter, iconAdapter, category, filter)
    }

    private fun deleteCategory(category: Category) {
        val transacParent = categoryAdapter.originalList.find { parent -> parent.list.contains(category) }

        // If the TransacParent is found, remove the transaction
        transacParent?.list?.remove(category)
        filter.filterCategoryName(categoryAdapter)
    }

    private fun showMoreCategoryPopup(category: Category, holder: CategoryHolder) {
        val popupMenu = PopupMenu(holder.itemView.context, holder.categoryMore)

        // Inflate the menu resource into the popup menu
        popupMenu.menuInflater.inflate(R.menu.popup_more, popupMenu.menu)
        popupMenu.gravity = Gravity.END

        //Set click listeners for the popup menu items
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    editCategory(category)
                    true
                }
                R.id.delete -> {
                    deleteCategory(category)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
}