package moe.cyunrei.videolivewallpaper.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.service.VideoLiveWallpaperService

class ImageViewActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoUriString = intent.getStringExtra("VIDEO_URI")

        if (videoUriString != null) {
            val videoUri = Uri.parse(videoUriString)
            videoView.setVideoURI(videoUri)
            videoView.start()
        }


        // Set up the 'Set As' button
        val buttonSetAs = findViewById<Button>(R.id.buttonSetAs)
        buttonSetAs.setOnClickListener {
            showSetAsOptions()
        }

        // Set up the back button
        val backButton = findViewById<MaterialButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun captureFrameFromVideo(videoPath: String): Bitmap? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        return try {
            mediaMetadataRetriever.setDataSource(this, Uri.parse(videoPath))
            // You might need to specify a particular time in microseconds
            mediaMetadataRetriever.getFrameAtTime(-1)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            mediaMetadataRetriever.release()
        }
    }

    private fun showSetAsOptions() {
        val options = arrayOf("Wallpaper", "Lock Screen", "Both")
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog) // Apply the custom style
        builder.setTitle("Set As")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> setAsWallpaper()
                1 -> setAsLockScreen()
                2 -> setAsBoth()
            }
        }
        // Add a "Cancel" button to the dialog
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun setAsWallpaper() {
        val videoUriString = intent.getStringExtra("VIDEO_URI") ?: return
        val intent = Intent(this, VideoLiveWallpaperService::class.java).apply {
            putExtra("VIDEO_PATH", videoUriString)
        }
        startService(intent)
        // Get the WallpaperManager instance
        val wallpaperManager = WallpaperManager.getInstance(this)

        // Clear existing wallpapers
        wallpaperManager.clear()

        // Set the live wallpaper component
        val componentName = ComponentName(this, VideoLiveWallpaperService::class.java)
        Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
            putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, componentName)
            startActivity(this)
        }

        showSuccessAlert("Wallpaper set successfully.")

    }

    private fun setAsLockScreen() {
        // ... your existing code to set lock screen ...
        showSuccessAlert("Lock screen set successfully.")
    }

    private fun setAsBoth() {
        // ... your existing code to set both ...
        showSuccessAlert("Wallpaper and lock screen set successfully.")
    }


    private fun showSuccessAlert(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Other methods...
}
