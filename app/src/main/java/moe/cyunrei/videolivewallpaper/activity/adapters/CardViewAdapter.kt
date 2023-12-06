package moe.cyunrei.videolivewallpaper.activity.adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.ImageViewActivity


class CardViewAdapter(items: List<WallpaperItem>) :
    RecyclerView.Adapter<CardViewAdapter.ViewHolder>() {
    private val items // Replace WallpaperItem with your data model class
            : List<WallpaperItem>

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: WallpaperItem = items[position]
        // Bind data to your views
        holder.imageView.setImageResource(item.ImageResource) // Example
        holder.cardView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ImageViewActivity::class.java)
            // Pass the image resource ID to the new activity
            intent.putExtra("IMAGE_RESOURCE", item.ImageResource)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var cardView:CardView
        init {
            imageView = itemView.findViewById<ImageView>(R.id.imageView)
            cardView = itemView.findViewById<CardView>(R.id.cardView)

        }
    }
    data class WallpaperItem(
        val ImageResource: Int,
    )
}
