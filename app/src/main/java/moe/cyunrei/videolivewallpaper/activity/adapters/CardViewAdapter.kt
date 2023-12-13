package moe.cyunrei.videolivewallpaper.activity.adapters


import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.ImageViewActivity
import moe.cyunrei.videolivewallpaper.activity.listners.PremiumItemListener
import moe.cyunrei.videolivewallpaper.service.VideoLiveWallpaperService
import java.io.File


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
        val context = holder.itemView.context
        val videoUri = Uri.parse(item.ImageResource)
        val videoFile = File(videoUri.path)
        val retriever = MediaMetadataRetriever()
        val thumbnail = getVideoThumbnail(holder.itemView.context, item.ImageResource)



        // Set the thumbnail as the image for the ImageView
        holder.imageView.setImageBitmap(thumbnail)
        // Handle click event on the card's image
        holder.imageView.setOnClickListener {
            if (isPremium) {
//                listener?.onPremiumItemClicked()
            } else {
                setAsWallpaper(item.ImageResource, holder.itemView.context)

            }
        }

        // Set the visibility of the premium icon depending on whether the item is premium
        holder.premiumIcon.visibility = if (item.isPremium) View.VISIBLE else View.GONE




    }

    private fun setAsWallpaper(videoUri: String, context: Context?) {
        val videoFilePath = context?.openFileOutput(
            "video_live_wallpaper_file_path",
            Context.MODE_PRIVATE
        ).use {
            if (it != null) {
                it.write(videoUri.toByteArray())
            }
        }

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

    private fun getVideoThumbnail(context: Context, videoPath: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, Uri.parse(videoPath))
            retriever.getFrameAtTime(-1)
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
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
        val ImageResource: String,
        val isPremium: Boolean // Indicates if the wallpaper is a premium item
    )
    /* data class WallpaperItem(
         val id: Int,
         val ImageResource: Int,
         val imageUrl: String,
         val isPremium: Boolean // Indicates if the wallpaper is a premium item
     )*/
}

private fun ImageView.setImageResource(imageResource: String) {

}
