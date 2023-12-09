package moe.cyunrei.videolivewallpaper.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.adapters.CardViewAdapter

class CategoryActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

       val categoryName = intent.getStringExtra("CATEGORY_NAME")
        //val textView: TextView = findViewById(R.id.category_name)
        //textView.text = categoryName

        // Set up the toolbar
        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.newActivityToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = categoryName
        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Sample data - replace with actual data
        val sampleData = listOf(
            CardViewAdapter.WallpaperItem("android.resource://" + this.packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + this.packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + this.packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + this.packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
// Add more items as needed
        )

        recyclerView.adapter = CardViewAdapter(sampleData, listener = null)





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
