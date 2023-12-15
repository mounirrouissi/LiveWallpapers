package moe.cyunrei.videolivewallpaper.activity.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetailsParams
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.activity.adapters.CardViewAdapter
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class PricingFragment : Fragment() {
    private lateinit var billingClient: BillingClient
    private val pendingPurchases = mutableListOf<Purchase>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pricing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        billingClient = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases() // Required as per Google Play Billing Library
            .setListener { billingResult, purchases ->
                // Handle the purchase
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready, you can query purchases here.
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
        // Setup click listeners for each pricing plan button
        view.findViewById<Button>(R.id.btn_single_purchase).setOnClickListener {
            choosePlan("Plan 1")
        }
        view.findViewById<Button>(R.id.btn_monthly_subscription).setOnClickListener {
            choosePlan("Plan 2")
        }

    }


    private fun choosePlan(planName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Your Selection")
            .setMessage("Are you sure you want to select $planName?")
            .setPositiveButton("Yes") { dialog, which ->
                initiatePurchaseFlow(planName)
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun initiatePurchaseFlow(planName: String) {
        val skuList = ArrayList<String>()
        skuList.add(planName) // Replace with actual SKU from Google Play Console
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList)
            .setType(BillingClient.SkuType.SUBS) // or SkuType.INAPP for one-time products
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    // Start the purchase flow
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build()
                    activity?.let { billingClient.launchBillingFlow(it, flowParams) }
                }
            }
        }
    }


    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Send purchase token to your server for verification
            //   sendPurchaseToServerForVerification(purchase.purchaseToken)

            // If you don't have a server, you can acknowledge the purchase directly, but it's less secure
            if (!purchase.isAcknowledged) {
                // Acknowledge the purchase
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Grant entitlement to the user
                        grantEntitlementToUser(purchase)
                    }
                }
            } else {
                // The purchase is already acknowledged, so just grant entitlement
                grantEntitlementToUser(purchase)
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            // The purchase is pending, handle it accordingly
            handlePendingPurchases()
            pendingPurchases.add(purchase)
        }
    }

    private fun grantEntitlementToUser(purchase: Purchase) {
        // Get the wallpaper information from the purchase
        val wallpaperId = purchase.skus.first()
        val wallpaperUrl = "url_of_the_wallpaper" // You need to replace this with the actual URL
        val isPremium = true // You need to replace this with the actual premium status

        // Get the SharedPreferences editor
        val sharedPref = requireContext().getSharedPreferences("MyAppSharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Store the wallpaper information in the shared preferences
        editor.putString("wallpaperId", wallpaperId)
        editor.putString("wallpaperUrl", wallpaperUrl)
        editor.putBoolean("isPremium", isPremium)

        // Commit the changes
        editor.apply()
    }




    private fun handlePendingPurchases() {
        for (purchase in pendingPurchases) {
            if (isConnectedToInternet() || isConnectedToInternetNetwork()) {
                // Try to acknowledge the purchase
                handlePurchase(purchase)
            }
        }
    }



    //utils methods

    private fun isConnectedToInternetNetwork(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    private fun isConnectedToInternet(): Boolean {
        return try {
            val sock = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)

            sock.connect(socketAddress, 2000)
            sock.close()

            true
        } catch (e: IOException) {
            false
        }
    }
    private fun handleAcknowledgePurchaseFailure(responseCode: Int) {
        // Show an error message to the user
        Toast.makeText(requireContext(), "Failed to acknowledge purchase: $responseCode", Toast.LENGTH_SHORT).show()
    }

    private fun handleUnexpectedPurchaseState(purchaseState: Int) {
        // Show an error message to the user
        Toast.makeText(requireContext(), "Unexpected purchase state: $purchaseState", Toast.LENGTH_SHORT).show()
    }

    private fun handleQueryPurchasesFailure(billingResult: BillingResult) {
        // Show an error message to the user
        Toast.makeText(requireContext(), "Failed to query purchases: ${billingResult.responseCode}", Toast.LENGTH_SHORT).show()
    }

 

    private fun updateUiForPendingPurchase(purchase: Purchase) {
        /*  // Find the UI element that corresponds to this purchase
          val wallpaperView = findWallpaperViewBySku(purchase.sku)
          // Create a ProgressBar to show the pending state
          val progressBar = ProgressBar(this)
          progressBar.isIndeterminate = true
          // Add the ProgressBar to the wallpaper view
          wallpaperView.addView(progressBar)*/

        Toast.makeText(requireContext(), "Your purchase is pending...", Toast.LENGTH_LONG).show()

    }

    companion object {
        fun getPurchasedWallpaper(context: Context): CardViewAdapter.WallpaperItem? {
            // Get the SharedPreferences
            val sharedPref = context.getSharedPreferences("MyAppSharedPrefs", Context.MODE_PRIVATE)


            // Retrieve the wallpaper information from the shared preferences
            val wallpaperId = sharedPref.getString("wallpaperId", "")
            val wallpaperUrl = sharedPref.getString("wallpaperUrl", null)
            val isPremium = sharedPref.getBoolean("isPremium", false)

            // Return the wallpaper
            return CardViewAdapter.WallpaperItem(wallpaperId, wallpaperUrl!!, isPremium, null)
        }

    }

}