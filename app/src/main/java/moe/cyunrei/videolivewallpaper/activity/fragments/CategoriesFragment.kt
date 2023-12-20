package moe.cyunrei.videolivewallpaper.activity.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.Category
import moe.cyunrei.videolivewallpaper.activity.adapters.CategoriesAdapter
import moe.cyunrei.videolivewallpaper.activity.CategoryActivity
import moe.cyunrei.videolivewallpaper.activity.listners.OnCategoryClickListener

class CategoriesFragment : Fragment(), OnCategoryClickListener {
    private lateinit var categoriesRecyclerView: RecyclerView
    private var adapter: CategoriesAdapter? = null
     lateinit var  categoriesList: List<Category>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.category_layout, container, false)

        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(context)


        categoriesList =  listOf(
                Category("Bugs Bunny", R.drawable.bunny1),
            Category("Tom and Jerry", R.drawable.tom),
            Category("Mickey Mouse", R.drawable.mickey),
            Category("The Simpsons", R.drawable.simpsons1),
            Category("Rick And Morty", R.drawable.rick),
            Category("Scooby Doo", R.drawable.scooby)
        )
        adapter = CategoriesAdapter(categoriesList, this)
        categoriesRecyclerView.adapter = adapter




        return view
    }
    override fun onCategoryClicked(category: Category) {
        val intent = Intent(context, CategoryActivity::class.java)
        intent.putExtra("CATEGORY_NAME", category.name)
        startActivity(intent)
    }

  /* override fun onCategoryClicked(categoryName: String) {
       (activity as? CategoryFragmentListener)?.onCategorySelected(categoryName)
   }*/
    interface CategoryFragmentListener {
        fun onCategorySelected(categoryName: String)
    }
}



/*

top most famous cartoons :
const cartoonTitles = [
"Mickey Mouse",
"Bugs Bunny",
"Tom and Jerry",
"Scooby-Doo, Where Are You!",
"The Simpsons",
"Looney Tunes",
"Popeye the Sailor",
"Dragon Ball",
"South Park",
"Avatar: The Last Airbender"
];*/
