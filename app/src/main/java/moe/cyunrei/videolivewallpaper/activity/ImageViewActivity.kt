package moe.cyunrei.videolivewallpaper.activity

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import moe.cyunrei.videolivewallpaper.R
import java.io.IOException

class ImageViewActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        // Get the image resource ID passed from the previous activity
        val imageResId = intent.getIntExtra("IMAGE_RESOURCE", 0)

        // Set the image resource to the ImageView
        val imageViewFull = findViewById<ImageView>(R.id.imageViewFull)
        imageViewFull.setImageResource(imageResId)

        // Set up the 'Set As' button
        val buttonSetAs = findViewById<Button>(R.id.buttonSetAs)
        buttonSetAs.setOnClickListener {
        showSetAsOptions()
        }
        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            // Finish the activity to return to the previous screen
            finish()
        }
    }

    private fun showSetAsOptions() {
        val options = arrayOf("Wallpaper", "Lock Screen", "Both")
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog) // Apply the custom style
        builder.setTitle("Set Image As")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> setAsWallpaper()
                1 -> setAsLockScreen()
                2 -> setAsBoth()
            }
        }
        builder.show()
    }

    private fun setAsWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        try {
            val imageView = findViewById<ImageView>(R.id.imageViewFull)
            val myBitmap = getBitmapFromImageView(imageView)
            myBitmap?.let {
                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                try {
                    wallpaperManager.setBitmap(it)
                } catch (e: IOException) {
                    e.printStackTrace()
                    // Handle the error here
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setAsLockScreen() {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        val imageView = findViewById<ImageView>(R.id.imageViewFull)
        val myBitmap = getBitmapFromImageView(imageView)

        try {
            myBitmap?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(it, null, true, WallpaperManager.FLAG_LOCK)
                } else {
                    // For older versions, you can't set the lock screen wallpaper directly
                    // You might want to inform the user or handle this case differently
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the error here
        }
    }


    private fun setAsBoth() {
       setAsLockScreen()
       setAsWallpaper()
    }
    private fun getBitmapFromImageView(imageView: ImageView): Bitmap? {
        return (imageView.drawable as? BitmapDrawable)?.bitmap
    }

}
