package moe.cyunrei.videolivewallpaper.activity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R

class WallpaperAdapter(private val wallpaperList: List<WallpaperItem>) :
        RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder>() {

        class WallpaperViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val imageView: ImageView = view.findViewById(R.id.imageView)
                // Initialize other views
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_wallpaper, parent, false)
                return WallpaperViewHolder(view)
        }

        override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
                val currentItem = wallpaperList[position]
//                holder.imageView.setImageResource(currentItem.id)
                holder.imageView.setImageResource(R.drawable.animal) // Replace with your image
                // Set other views
        }

        override fun getItemCount() = wallpaperList.size


        data class WallpaperItem(
                val id: Int,
                val title: String,
                val imageUrl: String,
                val isPremium: Boolean // Indicates if the wallpaper is a premium item
        )

}
