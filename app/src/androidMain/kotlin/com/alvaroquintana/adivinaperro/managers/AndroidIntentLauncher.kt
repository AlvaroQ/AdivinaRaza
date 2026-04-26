package com.alvaroquintana.adivinaperro.managers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.net.toUri
import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.utils.shareApp

class AndroidIntentLauncher(private val activityProvider: () -> Activity) : IntentLauncher {

    override fun shareApp(appName: String, shareMessageBody: String, chooseLabel: String) {
        shareApp(
            context = activityProvider(),
            appName = appName,
            shareMessageBody = shareMessageBody,
            chooseLabel = chooseLabel
        )
    }

    override fun rateApp() {
        val activity = activityProvider()
        val market = "market://details?id=${BuildConfig.APPLICATION_ID}".toUri()
        val intent = Intent(Intent.ACTION_VIEW, market).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
        }
        try {
            activity.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".toUri()
                )
            )
        }
    }

    override fun openPrivacyPolicy() {
        val activity = activityProvider()
        activity.startActivity(
            Intent(Intent.ACTION_VIEW, "https://alvaroq.github.io/privacy.html".toUri())
        )
    }
}
