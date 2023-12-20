import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import moe.cyunrei.videolivewallpaper.activity.adapters.CardViewAdapter
import moe.cyunrei.videolivewallpaper.activity.fragments.HomeFragment
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class WallpaperFetcher(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("wallpaper_data", Context.MODE_PRIVATE)
    private val cachedData = sharedPreferences.getString("wallpaper_data", null)

    suspend fun fetchWallpaperDataFromR2(bucket_name: String?="latest"): Flow<CardViewAdapter.WallpaperItem> {
        return if (bucket_name == "latest" && cachedData != null) {
            handleCachedData(cachedData,bucket_name)
        } else {
            bucket_name?.let { fetchDataFromServer(it) } ?: emptyFlow()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private suspend fun handleCachedData(cachedData: String, bucket_name: String?): Flow<CardViewAdapter.WallpaperItem> {
        val wallpaperItems = parseCachedData(cachedData)
            HomeFragment.wallpaperData.postValue(wallpaperItems.toList())
        return wallpaperItems
    }

    private fun parseCachedData(cachedData: String): Flow<CardViewAdapter.WallpaperItem> {
        return Gson().fromJson<List<CardViewAdapter.WallpaperItem>>(
            cachedData,
            object : TypeToken<List<CardViewAdapter.WallpaperItem>>() {}.type
        ).asFlow()
    }

    suspend fun fetchDataFromServer(bucket_name:String): Flow<CardViewAdapter.WallpaperItem> = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val token = HomeFragment.TOKEN_ID

        val request = Request.Builder()
            .url("https://api.cloudflare.com/client/v4/accounts/${HomeFragment.ACCOUNT_ID}/r2/buckets/${bucket_name}/objects")
            .get()
            .addHeader("X-Auth-Email", "mounirrouissi2@gmail.com")
            .addHeader("X-Auth-Key", "${token}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            handleServerResponse(response,bucket_name)
        }
    }

    private suspend fun handleServerResponse(response: Response, bucket_name: String): Flow<CardViewAdapter.WallpaperItem> {
        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response}")
        }

        val responseBody = response.body?.string()
        Log.d("MainActivity", "Response: $responseBody")

        val wallpaperItems = parseServerResponse(responseBody,bucket_name)
        if (bucket_name == "latest")
            updateSharedPreferences(wallpaperItems.toList())
        return wallpaperItems
    }

    private suspend fun parseServerResponse(responseBody: String?, bucket_name: String): Flow<CardViewAdapter.WallpaperItem> {
        val jsonObject = JSONObject(responseBody)
        val resultArray = jsonObject.getJSONArray("result")

        return List(resultArray.length()) { i ->
            val itemObject = resultArray.getJSONObject(i)
//            val videoFile: File = downloadVideoFile(itemObject,bucket_name)
            val videoFile = File(context.cacheDir, itemObject.getString("key") ?: "")

            val thumbnail = generateThumbnail(videoFile)

            CardViewAdapter.WallpaperItem(
                id = itemObject.getString("etag"),
                ImageResource = itemObject.getString("key"),
                thumbnail = thumbnail,
                isPremium = false // Set this value based on your criteria for premium items
            )
        }.asFlow()
    }

     fun downloadVideoFile(itemObject: JSONObject, bucket_name: String): File  {
        val videoFile = File(context.cacheDir, itemObject.getString("key") ?: "")
        val url =
            "https://api.cloudflare.com/client/v4/accounts/${HomeFragment.ACCOUNT_ID}/r2/buckets/${bucket_name}/objects/${itemObject.getString("key")}"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-Auth-Email", "mounirrouissi2@gmail.com")
            .addHeader("X-Auth-Key", "${HomeFragment.TOKEN_ID}")
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

    suspend fun generateThumbnail(videoFile: File): Bitmap? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoFile.absolutePath)
        retriever.getFrameAtTime(1000000)
    }

    private fun updateSharedPreferences(wallpaperItems: List<CardViewAdapter.WallpaperItem>) {
        val jsonString = Gson().toJson(wallpaperItems)
        sharedPreferences.edit().putString("wallpaper_data", jsonString).apply()
        HomeFragment.wallpaperData.postValue(wallpaperItems)
    }
}
