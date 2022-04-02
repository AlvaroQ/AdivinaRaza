package com.alvaroquintana.adivinaperro.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.utils.rateApp
import com.alvaroquintana.adivinaperro.utils.shareApp

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // rate_app
        val rateApp: Preference? = findPreference("rate_app")
        rateApp?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            rateApp(requireContext())
            false
        }

        // share
        val share: Preference? = findPreference("share")
        share?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            shareApp(requireContext(), -1)
            false
        }

        // Version
        val version: Preference? = findPreference("version")
        version?.summary = "${getString(R.string.settings_version)} ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})"

        // more_apps
        val moreApps: Preference? = findPreference("more_apps")
        moreApps?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            openAppOnPlayStore()
            false
        }
    }

    private fun openAppOnPlayStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=8877600766041307852")))
        } catch (notFoundException: ActivityNotFoundException) {

        }
    }
}