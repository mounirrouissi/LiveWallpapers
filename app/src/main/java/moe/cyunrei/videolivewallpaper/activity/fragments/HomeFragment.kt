package moe.cyunrei.videolivewallpaper.activity.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import moe.cyunrei.videolivewallpaper.R

import android.Manifest
import android.content.pm.PackageManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import moe.cyunrei.videolivewallpaper.activity.adapters.CardViewAdapter


class HomeFragment : Fragment() {
    companion object {
        private const val REQUEST_READ_STORAGE_PERMISSION = 1000
        const val CHOOSE_VIDEO_REQUEST_CODE = 1001

    }
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

        // Sample data - replace with actual data
        val sampleData = listOf(
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
            CardViewAdapter.WallpaperItem("android.resource://" + requireContext().packageName + "/" + R.raw.mobile_straw_hat_luffy, false),
// Add more items as needed
        )
        recyclerViewRecent.adapter = CardViewAdapter(sampleData, listener = null)

        permissionCheck()
        val chooseVideoButton = view.findViewById<FloatingActionButton>(R.id.fab)
        chooseVideoButton.setOnClickListener {
            chooseVideo()
        }

        /*view.findViewById<Button>(R.id.add_video_file_path).setOnClickListener {
            val edit = EditText(requireActivity())
            AlertDialog.Builder(requireActivity()).apply {
                setTitle(getString(R.string.add_path))
                setView(edit)
                setPositiveButton(getString(R.string.apply)) { _, _ ->
                    val videoFilePath: String = edit.text.toString()
                    requireActivity().openFileOutput(
                        "video_live_wallpaper_file_path",
                        Context.MODE_PRIVATE
                    ).use {
                        it.write(videoFilePath.toByteArray())
                    }
                    VideoLiveWallpaperService.setToWallPaper(requireActivity())
                }
                setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                setCancelable(true)
                create().apply {
                    setCanceledOnTouchOutside(true)
                    show()
                }
            }
        }

        view.findViewById<Button>(R.id.settings).setOnClickListener {
            startActivity(Intent(requireActivity(), SettingsActivity::class.java))
        }*/
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
    // Override onActivityResult if necessary

    private fun chooseVideo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "video/*"
        }
        startActivityForResult(intent, CHOOSE_VIDEO_REQUEST_CODE)
    }
}
