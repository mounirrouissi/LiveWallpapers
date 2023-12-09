package moe.cyunrei.videolivewallpaper.activity

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.service.VideoLiveWallpaperService

class WallpaperOptionsActivity : AppCompatActivity() {
    private var videoUriString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_options)
        videoUriString = intent.getStringExtra("VIDEO_URI")

        val buttonSetAs = findViewById<Button>(R.id.buttonSetAs)
        buttonSetAs.setOnClickListener {
            showSetAsOptions()
        }
    }

    private fun showSetAsOptions() {
        val options = arrayOf("Wallpaper", "Lock Screen", "Both")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Set As")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> setAsWallpaper()
                1 -> setAsLockScreen()
                2 -> setAsBoth()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun setAsBoth() {
        setAsWallpaper()
        setAsLockScreen()
    }

    private fun setAsLockScreen() {
        videoUriString?.let { videoUri ->
            val bitmap = captureFrameFromVideo(videoUri)
            bitmap?.let {
                try {
                    val wallpaperManager = WallpaperManager.getInstance(this)
                    // For API level 24 and above, you can set the lock screen wallpaper
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                        showMessage("Lock screen wallpaper set successfully.")
                    } else {
                        showMessage("Setting lock screen wallpaper is not supported on this device.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage("Failed to set lock screen wallpaper.")
                }
            } ?: showMessage("Error: Unable to extract frame from video.")
        } ?: showMessage("Error: Video path not found.")
    }

    private fun captureFrameFromVideo(videoPath: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(this, Uri.parse(videoPath))
            // Capture a frame. You might want to specify a time in microseconds
            retriever.getFrameAtTime(-1)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }


    private fun setAsWallpaper() {
        videoUriString?.let { videoUri ->
            // Save the video path for the service
            saveVideoPathForService(videoUri)

            // Prepare the intent to launch the live wallpaper chooser
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(this@WallpaperOptionsActivity, VideoLiveWallpaperService::class.java))
            }
            startActivity(intent)
        } ?: showMessage("Error: Video path not found.")
    }

    private fun saveVideoPathForService(videoPath: String) {
        val sharedPrefs = getSharedPreferences("LiveWallpaperPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("videoPath", videoPath).apply()
    }

    private fun showMessage(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}
