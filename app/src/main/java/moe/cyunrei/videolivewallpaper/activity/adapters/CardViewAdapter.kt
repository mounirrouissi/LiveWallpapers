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
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R

import moe.cyunrei.videolivewallpaper.activity.listners.PremiumItemListener
import moe.cyunrei.videolivewallpaper.service.VideoLiveWallpaperService

class CardViewAdapter(
    private val items: List<WallpaperItem>,
    private val isPremium: Boolean = false,
    private val listener: PremiumItemListener?
) : RecyclerView.Adapter<CardViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_card_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val thumbnail = getVideoThumbnail(holder.itemView.context, item.videoUri)
        holder.thumbnailImageView.setImageBitmap(thumbnail)

        holder.thumbnailImageView.setOnClickListener {
            if (isPremium) {
                listener?.onPremiumItemClicked()
            } else {
                setAsWallpaper(item.videoUri, holder.itemView.context)
                showMessage("Lock screen wallpaper set successfully." ,holder.itemView.context)
                /*val context = holder.itemView.context
                val intent = Intent(context, WallpaperOptionsActivity::class.java)
                intent.putExtra("VIDEO_URI", item.videoUri)
                context.startActivity(intent) */           }
        }

        holder.premiumIcon.visibility = if (item.isPremium) View.VISIBLE else View.GONE
    }

    private fun setAsWallpaper(videoUri: String, context: Context) {
        // Save the video path for the service
        saveVideoPathForService(context, videoUri)

        // Prepare the intent to launch the live wallpaper chooser
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(context, VideoLiveWallpaperService::class.java))
        }
        context.startActivity(intent)


    }



    private fun saveVideoPathForService(context: Context, videoPath: String) {
        val sharedPrefs = context.getSharedPreferences("LiveWallpaperPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("videoPath", videoPath).apply()
    }


    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnailImageView)
        var premiumIcon: ImageView = itemView.findViewById(R.id.premiumIcon)
        var cardView: CardView = itemView.findViewById(R.id.cardView)
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
    private fun showMessage(message: String, context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    data class WallpaperItem(
        val videoUri: String,
        val isPremium: Boolean
    )
}
