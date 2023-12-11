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
import androidx.core.content.ContextCompat.getSystemService
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetailsParams
import com.google.android.material.snackbar.Snackbar
import moe.cyunrei.videolivewallpaper.R
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
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS) // or SkuType.INAPP for one-time products
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
        when (purchase.signature) {
            "single_wallpaper_sku" -> {
                // Unlock the single wallpaper for the user
                unlockWallpaperForUser("")
            }
            "monthly_subscription_sku" -> {
                // Activate the monthly subscription for the user
                activateMonthlySubscriptionForUser(purchase)
            }
        }
    }

    private fun activateMonthlySubscriptionForUser(purchase: Purchase) {
        // Save the subscription status
        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("hasActiveSubscription", true)
        editor.apply()

        // Enable features associated with the subscription
        enableSubscriptionFeatures()

        // Keep track of the subscription expiration date
        val purchaseTime = purchase.purchaseTime
        val oneMonthInMillis = 30L * 24 * 60 * 60 * 1000
        val expirationDate = purchaseTime + oneMonthInMillis
        editor.putLong("subscriptionExpirationDate", expirationDate)
        editor.apply()


        // Handle subscription renewal or cancellation
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        // The subscription is still active
                        if (!purchase.isAcknowledged) {
                            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.purchaseToken)
                                .build()
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    // Update UI and handle successful acknowledgment
                                    updateUiWithActiveSubscription()
                                } else {
                                    // Handle acknowledgment failure
                                    handleAcknowledgePurchaseFailure(billingResult.responseCode)
                                }
                            }
                        } else {
                            // Update UI and handle existing active subscription
                            updateUiWithActiveSubscription()
                        }
                    } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                        // The subscription is pending, handle this case accordingly
                        handlePendingPurchases()
                    } /*else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                        // The subscription is cancelled, remove the subscription status
                        editor.putBoolean("hasActiveSubscription", false)
                        editor.apply()
                        //updateUiWithInactiveSubscription()
                    }*/ else {
                        // Handle any other unexpected purchase states
                        handleUnexpectedPurchaseState(purchase.purchaseState)
                    }
                }
            } else {
                // Handle query purchases failure
                handleQueryPurchasesFailure(billingResult)
            }
        }



    }

    private fun updateUiWithActiveSubscription() {
        TODO("Not yet implemented")
    }

    private fun enableSubscriptionFeatures() {
        TODO("Not yet implemented")
    }


    private fun unlockWallpaperForUser(wallpaperId: String) {
        // Save the purchased wallpaper ID in SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(wallpaperId, true)
        editor.apply()

        // Update the UI to reflect the purchase
     //   updateUIForPurchasedWallpaper(wallpaperId)

        // Download the wallpaper if necessary
       // downloadWallpaperIfNecessary(wallpaperId)
    }



    private fun sendPurchaseToServerForVerification(purchaseToken: String) {
/*
        // Create a new network request to your server
        val request = Request.Builder()
            .url("https://your-server.com/verify_purchase?purchaseToken=$purchaseToken")
            .build()

        // Send the request and handle the response
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the error
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // The purchase token was verified successfully
                    grantEntitlementToUser(purchase)
                } else {
                    // The purchase token verification failed
                }
            }
        })
*/
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

    private fun handlePendingPurchases() {
        for (purchase in pendingPurchases) {
            if (isConnectedToInternet() || isConnectedToInternetNetwork()) {
                // Try to acknowledge the purchase
                handlePurchase(purchase)
            }
            // Update the UI to reflect the pending state
            updateUiForPendingPurchase(purchase)
        }
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



}
