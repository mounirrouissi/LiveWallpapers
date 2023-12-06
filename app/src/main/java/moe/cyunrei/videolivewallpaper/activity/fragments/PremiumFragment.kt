package moe.cyunrei.videolivewallpaper.activity.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.adapters.CardViewAdapter

class PremiumFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_premium, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerViewRecent = view.findViewById<RecyclerView>(R.id.recyclerViewPremium)
        recyclerViewRecent.layoutManager = GridLayoutManager(context,2)

        // Sample data - replace with actual data
        val sampleData = listOf(
            CardViewAdapter.WallpaperItem(R.drawable.animal),
            CardViewAdapter.WallpaperItem(R.drawable.animal),
            CardViewAdapter.WallpaperItem(R.drawable.animal),
            CardViewAdapter.WallpaperItem(R.drawable.animal),// Replace 'dummy_image' with your image in drawable
            CardViewAdapter.WallpaperItem(R.drawable.animal),// Replace 'dummy_image' with your image in drawable
            CardViewAdapter.WallpaperItem(R.drawable.animal),// Replace 'dummy_image' with your image in drawable
            // Add more items as needed
        )
        recyclerViewRecent.adapter = CardViewAdapter(sampleData)
    }

}
