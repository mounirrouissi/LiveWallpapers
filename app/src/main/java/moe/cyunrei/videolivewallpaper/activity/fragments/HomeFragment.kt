package moe.cyunrei.videolivewallpaper.activity.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.adapters.CardViewAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment() {
    companion object {
        private const val REQUEST_READ_STORAGE_PERMISSION = 1000
        const val CHOOSE_VIDEO_REQUEST_CODE = 1001
    }

    private val wallpaperData = MutableLiveData<List<CardViewAdapter.WallpaperItem>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerViews
        val recyclerViewRecent = view.findViewById<RecyclerView>(R.id.recyclerViewRecent)
        recyclerViewRecent.layoutManager = GridLayoutManager(context,2)

        // Fetch the wallpaper data from the Cloudflare R2 bucket
        fetchWallpaperDataFromR2()

        // Observe the MutableLiveData object and update the RecyclerView when the data changes
        wallpaperData.observe(viewLifecycleOwner, Observer { data ->

            recyclerViewRecent.adapter = CardViewAdapter(data, listener = null)
        })

        permissionCheck()
        val chooseVideoButton = view.findViewById<FloatingActionButton>(R.id.fab)
        chooseVideoButton.setOnClickListener {
            chooseVideo()
        }
    }

    private fun fetchWallpaperDataFromR2() {
        val client = OkHttpClient()
        val account_id= "1079414e72226b3a1f5d5d5fc0adc423"
        val key_id = "71c3ca34b001567adf52c9f6315d95b0d2d61"
        val token = "71c3ca34b001567adf52c9f6315d95b0d2d61"
        val request = Request.Builder()
            .url("https://api.cloudflare.com/client/v4/accounts/${account_id}/r2/buckets/live1/objects")
            .get()
            .addHeader("X-Auth-Email", "mounirrouissi2@gmail.com")
            .addHeader("X-Auth-Key", "${token}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                val responseBody = response.body?.string()
                Log.d("MainActivity", "Response: $responseBody")

                // Parse the JSON response
                val jsonObject = JSONObject(responseBody)
                val resultArray = jsonObject.getJSONArray("result")

                // Convert the result array into a list of WallpaperItem
                val wallpaperItems = List(resultArray.length()) { i ->
                    val itemObject = resultArray.getJSONObject(i)
                    val videoFile = File(context!!.cacheDir, itemObject.getString("key"))

                    // Download the video file
                    val url = "https://api.cloudflare.com/client/v4/accounts/${account_id}/r2/buckets/live1/objects/${itemObject.getString("key")}"
                    val request = Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("X-Auth-Email", "mounirrouissi2@gmail.com")
                        .addHeader("X-Auth-Key", "${token}")
                        .build()
                    val client = OkHttpClient()
                    val response = client.newCall(request).execute()
                    response.body?.byteStream()?.use { input ->
                        FileOutputStream(videoFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Generate a thumbnail from the video file
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(videoFile.absolutePath)
                    val thumbnail = retriever.getFrameAtTime(1000000)

                    CardViewAdapter.WallpaperItem(
                        id = itemObject.getString("etag"),
                        ImageResource = itemObject.getString("key"),
                        thumbnail = thumbnail,
                        isPremium = false // Set this value based on your criteria for premium items
                    )
                }

                // Update the MutableLiveData object with the fetched data
                wallpaperData.postValue(wallpaperItems)
            }
        })
    }

    private fun permissionCheck() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_STORAGE_PERMISSION
            )
        }
    }

    private fun chooseVideo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "video/*"
        }
        startActivityForResult(intent, CHOOSE_VIDEO_REQUEST_CODE)
    }
}
