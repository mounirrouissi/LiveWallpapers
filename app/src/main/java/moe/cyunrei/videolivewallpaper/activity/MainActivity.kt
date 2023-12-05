package moe.cyunrei.videolivewallpaper.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.service.VideoLiveWallpaperService
import moe.cyunrei.videolivewallpaper.utils.DocumentUtils.getPath

class MainActivity : AppCompatActivity(),CategoriesFragment.CategoryFragmentListener  {
    private var isDarkTheme = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadThemeState()
        setAppropriateTheme()

        setContentView(R.layout.main_activity)
        /*permissionCheck
        findViewById<Button?>(R.id.choose_video_file).apply {
            setOnClickListener { chooseVideo() }
        }

        findViewById<Button?>(R.id.add_video_file_path).apply {
            setOnClickListener {
                val edit = EditText(this@MainActivity)
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle(getString(R.string.add_path))
                    setView(edit)
                    setPositiveButton(
                        getString(R.string.apply)
                    ) { _, _ ->
                        val videoFilePath: String = edit.text.toString()
                        this@MainActivity.openFileOutput(
                            "video_live_wallpaper_file_path",
                            Context.MODE_PRIVATE
                        ).use {
                            it.write(videoFilePath.toByteArray())
                        }
                        VideoLiveWallpaperService.setToWallPaper(this@MainActivity)
                    }
                    setNegativeButton(
                        getString(R.string.cancel)
                    ) { _, _ -> }
                    setCancelable(true)
                    create().apply {
                        setCanceledOnTouchOutside(true)
                        show()
                    }
                }
            }
        }

        findViewById<Button?>(R.id.settings).apply {
            setOnClickListener {
                Intent(this@MainActivity, SettingsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }*/
        setupBottomNavigation()
    }
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Load HomeFragment
                    loadFragment(HomeFragment())
                }
                R.id.navigation_categories -> {
                    // Load CategoriesFragment
                    loadFragment(CategoriesFragment())
                }
                R.id.navigation_prime -> {
                    // Load PrimeFragment or relevant fragment
//                    loadFragment(PrimeFragment())
                }
                R.id.navigation_settings -> {
                    // Load SettingsFragment
//                    loadFragment(SettingsFragment())
                }
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_theme -> {
                toggleTheme()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        saveThemeState(isDarkTheme)
        setAppropriateTheme()
    }

    private fun setAppropriateTheme() {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun saveThemeState(isDark: Boolean) {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("DARK_THEME", isDark)
            apply()
        }
    }

    private fun loadThemeState() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        isDarkTheme = sharedPref.getBoolean("DARK_THEME", false)
    }

    private val permissionCheck: Unit
        get() {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
            )
        }

    private fun chooseVideo() {
        Intent().apply {
            type = "video/*"
            action = Intent.ACTION_GET_CONTENT
        }.also { startActivityForResult(it, 1) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val uri: Uri = data?.data!!
            this.openFileOutput(
                "video_live_wallpaper_file_path",
                Context.MODE_PRIVATE
            ).use {
                it.write(getPath(this, uri)!!.toByteArray())
            }
            VideoLiveWallpaperService.setToWallPaper(this)
        }
    }

    override fun onCategorySelected(categoryName: String) {
        TODO("Not yet implemented")
    }
}
