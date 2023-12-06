package moe.cyunrei.videolivewallpaper.activity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.OnCategoryClickListener

class CategoriesAdapter(
        private val categoriesList: List<String>,
        private val clickListener: OnCategoryClickListener
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
                return CategoryViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
                val category = categoriesList[position]
                holder.categoryName.text = category
                holder.categoryImage.setImageResource(R.drawable.animal) // Replace with your image
                holder.itemView.setOnClickListener { clickListener.onCategoryClicked(category) }
        }

        class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val categoryName: TextView = view.findViewById(R.id.category_name)
                val categoryImage: ImageView = view.findViewById(R.id.category_background_image)
        }

        override fun getItemCount(): Int {
                return categoriesList.size
        }


}
