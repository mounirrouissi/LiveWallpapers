package moe.cyunrei.videolivewallpaper.activity.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.adapters.CardViewAdapter
import moe.cyunrei.videolivewallpaper.activity.fragments.ProgressPointsView.ProgressPointsView
import moe.cyunrei.videolivewallpaper.utils.MethodsUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val ACCOUNT_ID = "1079414e72226b3a1f5d5d5fc0adc423"

private const val TOKEN_ID = "71c3ca34b001567adf52c9f6315d95b0d2d61"

class HomeFragment : Fragment() {
    companion object {
        private const val REQUEST_READ_STORAGE_PERMISSION = 1000
        const val CHOOSE_VIDEO_REQUEST_CODE = 1001
    }

    private lateinit var wallpaperItems: List<CardViewAdapter.WallpaperItem>
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
        val progressPointsView = view.findViewById<ProgressPointsView>(R.id.progressPointsView)
        val loadingMessage = view.findViewById<TextView>(R.id.loadingMessage)

        recyclerViewRecent.layoutManager = GridLayoutManager(context,2)

        // Show the progress points view and the loading message
        progressPointsView.visibility = View.VISIBLE

        loadingMessage.visibility = View.VISIBLE
        progressPointsView.setPoints(3)

        // Fetch the wallpaper data from the Cloudflare R2 bucket
        if (MethodsUtils.isNetworkAvailable(requireContext())) {
            fetchWallpaperDataFromR2()
        } else {
            // Show an alert to the user
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }

        // Observe the MutableLiveData object and update the RecyclerView when the data changes
        wallpaperData.observe(viewLifecycleOwner, Observer { data ->
            recyclerViewRecent.adapter = CardViewAdapter(data, listener = null)

            // Hide the progress points view and the loading message
            loadingMessage.visibility = View.GONE
            progressPointsView.visibility = View.GONE

        })

        permissionCheck()
        val chooseVideoButton = view.findViewById<FloatingActionButton>(R.id.fab)
        chooseVideoButton.setOnClickListener {
            chooseVideo()
        }
    }


    private fun fetchWallpaperDataFromR2() {
        val sharedPreferences = context?.getSharedPreferences("wallpaper_data", Context.MODE_PRIVATE)
        val cachedData = sharedPreferences?.getString("wallpaper_data", null)

        if (cachedData != null) {
            handleCachedData(cachedData)
        } else {
            fetchDataFromServer()
        }
    }

    private fun handleCachedData(cachedData: String) {
        val wallpaperItems = parseCachedData(cachedData)
        wallpaperData.postValue(wallpaperItems)
    }

    private fun parseCachedData(cachedData: String): List<CardViewAdapter.WallpaperItem> {
        return Gson().fromJson<List<CardViewAdapter.WallpaperItem>>(
            cachedData,
            object : TypeToken<List<CardViewAdapter.WallpaperItem>>() {}.type
        )
    }

    private fun fetchDataFromServer() {
        val client = OkHttpClient()
        val token = TOKEN_ID

        val request = Request.Builder()
            .url("https://api.cloudflare.com/client/v4/accounts/${ACCOUNT_ID}/r2/buckets/live1/objects")
            .get()
            .addHeader("X-Auth-Email", "mounirrouissi2@gmail.com")
            .addHeader("X-Auth-Key", "${token}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                handleServerResponse(response)
            }
        })
    }

    private fun handleServerResponse(response: Response) {
        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response}")
        }

        val responseBody = response.body?.string()
        Log.d("MainActivity", "Response: $responseBody")

        val wallpaperItems = parseServerResponse(responseBody)
        updateSharedPreferences(wallpaperItems)
    }

    private fun parseServerResponse(responseBody: String?): List<CardViewAdapter.WallpaperItem> {
        val jsonObject = JSONObject(responseBody)
        val resultArray = jsonObject.getJSONArray("result")

        return List(resultArray.length()) { i ->
            val itemObject = resultArray.getJSONObject(i)
            val videoFile: File = File(context?.cacheDir, itemObject.getString("key") ?: "")
            val thumbnail = generateThumbnail(videoFile)

            CardViewAdapter.WallpaperItem(
                id = itemObject.getString("etag"),
                ImageResource = itemObject.getString("key"),
                thumbnail = thumbnail,
                isPremium = false // Set this value based on your criteria for premium items
            )
        }
    }

    private fun downloadVideoFile(itemObject: JSONObject): File {
        val videoFile = File(context?.cacheDir, itemObject.getString("key") ?: "")
        val url =
            "https://api.cloudflare.com/client/v4/accounts/${ACCOUNT_ID}/r2/buckets/live1/objects/${itemObject.getString("key")}"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-Auth-Email", "mounirrouissi2@gmail.com")
            .addHeader("X-Auth-Key", "${TOKEN_ID}")
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()
        response.body?.byteStream()?.use { input ->
            FileOutputStream(videoFile).use { output ->
                input.copyTo(output)
            }
        }
        return videoFile
    }

    private fun generateThumbnail(videoFile: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoFile.absolutePath)
        return retriever.getFrameAtTime(1000000)
    }

    private fun updateSharedPreferences(wallpaperItems: List<CardViewAdapter.WallpaperItem>) {
        val jsonString = Gson().toJson(wallpaperItems)
        val sharedPreferences = context?.getSharedPreferences("wallpaper_data", Context.MODE_PRIVATE)
        sharedPreferences?.edit()?.putString("wallpaper_data", jsonString)?.apply()
        wallpaperData.postValue(wallpaperItems)
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
