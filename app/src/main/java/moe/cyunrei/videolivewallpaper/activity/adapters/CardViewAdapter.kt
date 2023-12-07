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
import moe.cyunrei.videolivewallpaper.activity.listners.PremiumItemListener


class CardViewAdapter(
    items: List<WallpaperItem>, private val isPremium: Boolean = false,
    private val listener: PremiumItemListener?
) :
    RecyclerView.Adapter<CardViewAdapter.ViewHolder>() {
    private val items // Replace WallpaperItem with your data model class
            : List<WallpaperItem>

    init {
        this.items = items
        println("PREMIUM =========$isPremium")
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

        // Handle click event on the card's image
        holder.imageView.setOnClickListener {
            // If the clicked item is premium, invoke the listener's method to handle the premium click
            if (isPremium) {
                listener?.onPremiumItemClicked()
            } else {
                // For non-premium items, continue with the normal flow
                val context = holder.itemView.context
                val intent = Intent(context, ImageViewActivity::class.java)
                intent.putExtra("IMAGE_RESOURCE", item.ImageResource)
                context.startActivity(intent)
            }
        }

        // Set the visibility of the premium icon depending on whether the item is premium
        holder.premiumIcon.visibility = if (item.isPremium) View.VISIBLE else View.GONE




    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var premiumIcon: ImageView
        var cardView:CardView
        init {
            imageView = itemView.findViewById<ImageView>(R.id.imageView)
            premiumIcon = itemView.findViewById<ImageView>(R.id.premiumIcon)
            cardView = itemView.findViewById<CardView>(R.id.cardView)

        }
    }
    data class WallpaperItem(
        val ImageResource: Int,
        val isPremium: Boolean // Indicates if the wallpaper is a premium item
    )
   /* data class WallpaperItem(
        val id: Int,
        val ImageResource: Int,
        val imageUrl: String,
        val isPremium: Boolean // Indicates if the wallpaper is a premium item
    )*/
}
