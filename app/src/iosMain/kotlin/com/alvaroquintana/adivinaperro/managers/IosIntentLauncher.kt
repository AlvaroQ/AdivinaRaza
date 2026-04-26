package com.alvaroquintana.adivinaperro.managers

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosIntentLauncher : IntentLauncher {
    override fun shareApp(appName: String, shareMessageBody: String, chooseLabel: String) {
        // Phase 6c will wire UIActivityViewController via the iOS host
        // (UIWindowScene.keyWindow.rootViewController?.present(...))
    }

    override fun rateApp() {
        NSURL.URLWithString(
            "https://apps.apple.com/app/id000000000"
        )?.let { UIApplication.sharedApplication.openURL(it) }
    }

    override fun openPrivacyPolicy() {
        NSURL.URLWithString(
            "https://alvaroq.github.io/privacy.html"
        )?.let { UIApplication.sharedApplication.openURL(it) }
    }
}
