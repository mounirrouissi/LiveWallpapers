package moe.cyunrei.videolivewallpaper.activity.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.adapters.CardViewAdapter

import moe.cyunrei.videolivewallpaper.activity.listners.PremiumItemListener

class PremiumFragment : Fragment(),PremiumItemListener {

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
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, true),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, true),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, true),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, true),
// Add more items as needed
        )
        recyclerViewRecent.adapter = CardViewAdapter(sampleData, listener = null)
    }

    override fun onPremiumItemClicked() {
        // Navigate to the PricingFragment or Activity
        // If using a FragmentManager to handle the navigation
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, PricingFragment())
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

}
