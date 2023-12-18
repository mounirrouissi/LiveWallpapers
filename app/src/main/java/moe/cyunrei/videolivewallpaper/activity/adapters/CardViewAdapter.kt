package moe.cyunrei.videolivewallpaper.activity.adapters


import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.fragments.PricingFragment
import moe.cyunrei.videolivewallpaper.activity.listners.PremiumItemListener
import moe.cyunrei.videolivewallpaper.service.VideoLiveWallpaperService
import java.io.File


class CardViewAdapter(
    items: List<WallpaperItem>, private val isPremium: Boolean = false,
    private val listener: PremiumItemListener?
) : RecyclerView.Adapter<CardViewAdapter.ViewHolder>() {

    private val items: List<WallpaperItem> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_card_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: WallpaperItem = items[position]
        val context = holder.itemView.context
        val thumbnail = item.thumbnail

        // Set the thumbnail as the image for the ImageView
        holder.imageView.setImageBitmap(thumbnail)

        // Handle click event on the card's image
        holder.imageView.setOnClickListener {
            handleWallpaperClick(item, context, holder)
        }

        // Set the visibility of the premium icon depending on whether the item is premium
        holder.premiumIcon.visibility = if (item.isPremium) View.VISIBLE else View.GONE
    }

    private fun handleWallpaperClick(
        item: WallpaperItem,
        context: Context,
        holder: ViewHolder
    ) {
        if (item.isPremium) {
            // The wallpaper is premium
            val purchasedWallpaper = PricingFragment.getPurchasedWallpaper(context)
            if (purchasedWallpaper != null && purchasedWallpaper.id == item.id) {
                // The wallpaper has already been purchased, so set it directly
                setAsWallpaper(item.ImageResource, holder.itemView.context)
            } else {
                // The wallpaper has not been purchased, so show the pricing dialogue
                listener?.onPremiumItemClicked()
            }
        } else {
            // Save the video path for the service
            holder.itemView.context.openFileOutput("video_live_wallpaper_file_path", Context.MODE_PRIVATE).use {
                it.write(Uri.parse(item.ImageResource).toString().toByteArray())
            }

            // The wallpaper is not premium, so set it as the wallpaper
            setAsWallpaper(item.ImageResource, holder.itemView.context)
        }
    }

    private fun setAsWallpaper(videoUri: String, context: Context?) {
        val fileName = "video_live_wallpaper_file_path"
        context?.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it?.write(videoUri.toByteArray())
        }

        // Prepare the intent to launch the live wallpaper chooser
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                context?.let { ComponentName(it, VideoLiveWallpaperService::class.java) })
            putExtra("video_file_path", fileName) // Pass the name of the file
        }
        context?.startActivity(intent)
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
        val id: String?,
        val ImageResource: String,
        val isPremium: Boolean, // Indicates if the wallpaper is a premium item
        val thumbnail: Bitmap?
    )
}

