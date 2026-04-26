package com.alvaroquintana.adivinaperro.managers

interface IntentLauncher {
    fun shareApp(appName: String, shareMessageBody: String, chooseLabel: String)
    fun rateApp()
    fun openPrivacyPolicy()
}
