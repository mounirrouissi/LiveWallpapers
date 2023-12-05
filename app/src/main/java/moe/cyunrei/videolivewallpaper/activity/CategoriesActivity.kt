package moe.cyunrei.videolivewallpaper.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R

class CategoriesActivity : AppCompatActivity(), OnCategoryClickListener {
    private lateinit var categoriesRecyclerView: RecyclerView
    private var adapter: CategoriesAdapter? = null
    val categoriesList: List<String> = mutableListOf("Animals", "Animals", "Nature")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_layout)

        categoriesRecyclerView = findViewById(R.id.categories_recycler_view)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Setting the adapter with the click listener
        adapter = CategoriesAdapter(categoriesList, this)
        categoriesRecyclerView.adapter = adapter
    }

    override fun onCategoryClicked(categoryName: String) {
        val intent = Intent(this, NewActivity::class.java)
        intent.putExtra("CATEGORY_NAME", categoryName)
        startActivity(intent)
    }
}
