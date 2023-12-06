package moe.cyunrei.videolivewallpaper.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import moe.cyunrei.videolivewallpaper.R

class NewActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        val categoryName = intent.getStringExtra("CATEGORY_NAME")
        val textView: TextView = findViewById(R.id.category_name)
        textView.text = categoryName

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.newActivityToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = categoryName

        // Handling back button press
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish() // or your custom logic
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)


    }


}
