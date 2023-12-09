package moe.cyunrei.videolivewallpaper.activity.fragments

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

class PricingFragment : Fragment() {
    private lateinit var billingClient: BillingClient

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

    private fun handlePurchase(purchase: Purchase?) {
        TODO("Not yet implemented")
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
            sendPurchaseToServerForVerification(purchase.purchaseToken)

            // If you don't have a server, you can acknowledge the purchase directly, but it's less secure
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Grant entitlement to the user
                        grantEntitlementToUser(purchase)
                    }
                }
            }
        }
    }

    private fun grantEntitlementToUser(purchase: Purchase) {
        when (purchase.sku) {
            "single_wallpaper_sku" -> {
                // Unlock the single wallpaper for the user
                unlockWallpaperForUser()
            }
            "monthly_subscription_sku" -> {
                // Activate the monthly subscription for the user
                activateMonthlySubscriptionForUser()
            }
        }
    }

    private fun activateMonthlySubscriptionForUser() {
        TODO("Not yet implemented")
    }

    private fun unlockWallpaperForUser() {
        TODO("Not yet implemented")
    }


    private fun sendPurchaseToServerForVerification(purchaseToken: String) {
        // Send the purchase token to your server
        // Your server should then validate the purchase with Google Play
    }


}
