package moe.cyunrei.videolivewallpaper.activity.fragments

import android.content.ComponentName
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.Preference
import moe.cyunrei.videolivewallpaper.R
import moe.cyunrei.videolivewallpaper.service.VideoLiveWallpaperService
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import moe.cyunrei.videolivewallpaper.activity.MainActivity
import java.io.IOException

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        // Find and set up the rate app preference
        findPreference<Preference>(getString(R.string.rate_app_key))?.setOnPreferenceClickListener {
            openAppInPlayStore()
            true
        }

        // Set up sound preference
        setupSoundPreference()

        // Set up hide icon from launcher preference
        setupIconVisibilityPreference()
    }

    private fun setupSoundPreference() {
        findPreference<SwitchPreferenceCompat>(getString(R.string.preference_play_video_with_sound))?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                isChecked = !isChecked
                if (isChecked) {
                    VideoLiveWallpaperService.unmuteMusic(requireContext())
                    executeCommand("touch ${requireContext().filesDir}/unmute")
                } else {
                    VideoLiveWallpaperService.muteMusic(requireContext())
                    executeCommand("rm ${requireContext().filesDir}/unmute")
                }
                false
            }
        }
    }

    private fun setupIconVisibilityPreference() {
        findPreference<SwitchPreferenceCompat>(getString(R.string.preference_hide_icon_from_launcher))?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                val packageManager = requireActivity().packageManager
                val componentName = ComponentName(requireContext(), MainActivity::class.java)

                if (isChecked) {
                    packageManager.setComponentEnabledSetting(
                        componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                } else {
                    packageManager.setComponentEnabledSetting(
                        componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
                isChecked = !isChecked
                false
            }
        }
    }

    private fun openAppInPlayStore() {
        val appPackageName = requireContext().packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    private fun executeCommand(command: String) {
        try {
            Runtime.getRuntime().exec(command)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
