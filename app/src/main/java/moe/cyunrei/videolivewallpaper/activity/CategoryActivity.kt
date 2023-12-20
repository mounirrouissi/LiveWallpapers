package moe.cyunrei.videolivewallpaper.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.adapters.CardViewAdapter
import moe.cyunrei.videolivewallpaper.utils.MethodsUtils
import moe.cyunrei.videolivewallpaper.utils.WallpaperFetcher

class CategoryActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

       var categoryName = intent.getStringExtra("CATEGORY_NAME")

        //val textView: TextView = findViewById(R.id.category_name)
        //textView.text = categoryName

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.newActivityToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = categoryName
        // Initialize RecyclerView

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        val wallpaperFetcher = WallpaperFetcher(context = this)
        // Sample data - replace with actual data retrieved from R2 using the category name as bucket name:
        var sampleData: Unit? = null

        CoroutineScope(Dispatchers.Main).launch {
            val sampleData = wallpaperFetcher.fetchWallpaperDataFromR2(MethodsUtils.formatCategoryTitle(categoryName))

            // Set the adapter after the data is fetched
            recyclerView.adapter = CardViewAdapter(sampleData, listener = null)
        }





    }

    override fun onSupportNavigateUp(): Boolean {

        super.onBackPressed() // This will handle the default back action
        return true
    }
    override fun onBackPressed() {
        // Additional logic if needed
        super.onBackPressed() // This will handle the default back action
    }

}
