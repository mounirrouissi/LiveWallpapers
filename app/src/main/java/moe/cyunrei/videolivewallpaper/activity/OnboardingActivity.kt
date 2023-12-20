package moe.cyunrei.videolivewallpaper.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.adapters.OnboardingAdapter
import moe.cyunrei.videolivewallpaper.utils.MethodsUtils
import moe.cyunrei.videolivewallpaper.utils.WallpaperFetcher

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = OnboardingAdapter(this)

        // Fetch the wallpaper data from the Cloudflare R2 bucket
        if (MethodsUtils.isNetworkAvailable(this)) {
            val wallpaperFetcher = WallpaperFetcher(context = this)
            scope.launch {
                wallpaperFetcher.fetchWallpaperDataFromR2("latest")
            }
        } else {
            // Show an alert to the user
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Cancel the coroutine when the activity is destroyed
    }


}
