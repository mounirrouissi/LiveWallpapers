package moe.cyunrei.videolivewallpaper.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R

class CategoriesFragment : Fragment(), OnCategoryClickListener {
    private lateinit var categoriesRecyclerView: RecyclerView
    private var adapter: CategoriesAdapter? = null
    private val categoriesList: List<String> = mutableListOf("Animals", "Animes", "Nature")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.category_layout, container, false)

        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter = CategoriesAdapter(categoriesList, this)
        categoriesRecyclerView.adapter = adapter

        return view
    }
    override fun onCategoryClicked(categoryName: String) {
        val intent = Intent(context, NewActivity::class.java)
        intent.putExtra("CATEGORY_NAME", categoryName)
        startActivity(intent)
    }

  /* override fun onCategoryClicked(categoryName: String) {
       (activity as? CategoryFragmentListener)?.onCategorySelected(categoryName)
   }*/
    interface CategoryFragmentListener {
        fun onCategorySelected(categoryName: String)
    }

}
