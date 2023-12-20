package moe.cyunrei.videolivewallpaper.utils

import android.content.Context
import android.net.ConnectivityManager

class MethodsUtils {


    companion object {
        public fun isNetworkAvailable(context : Context): Boolean {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        fun formatCategoryTitle(input: String?): String {
            return input?.replace(" ", "")!!.toLowerCase()
        }

    }

}