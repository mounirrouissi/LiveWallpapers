package moe.cyunrei.videolivewallpaper.activity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import moe.cyunrei.videolivewallpaper.R

class PricingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pricing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup click listeners for each pricing plan button
        view.findViewById<Button>(R.id.btn_single_purchase).setOnClickListener {
            choosePlan("Plan 1")
        }
        view.findViewById<Button>(R.id.btn_monthly_subscription).setOnClickListener {
            choosePlan("Plan 2")
        }

    }

    private fun choosePlan(planName: String) {
        // Show a confirmation dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Your Selection")
            .setMessage("Are you sure you want to select $planName?")
            .setPositiveButton("Yes") { dialog, which ->
                // Handle the confirmation
                handlePlanSelection(planName)
            }
            .setNegativeButton("No") { dialog, which ->
                // Dismiss the dialog and do nothing
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun handlePlanSelection(planName: String) {
        // Handle the selected plan logic here
        // For example, start an activity for purchase process or update user's subscription status
        Toast.makeText(activity, "Proceeding with $planName", Toast.LENGTH_SHORT).show()
        // Add your own logic to proceed with the selected plan
    }
}
