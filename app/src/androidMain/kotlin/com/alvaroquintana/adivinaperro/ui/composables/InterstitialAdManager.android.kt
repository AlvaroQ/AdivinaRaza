package com.alvaroquintana.adivinaperro.ui.composables

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AndroidInterstitialAdState(
    private val activity: Activity,
    private val adUnitId: String,
    private val adLocation: String
) : InterstitialAdState {
    var interstitialAd: InterstitialAd? = null
        private set

    private var isLoading = false
    private var retryCount = 0
    private val maxRetries = 2

    fun load() {
        if (isLoading || interstitialAd != null) return
        isLoading = true

        InterstitialAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    if (BuildConfig.DEBUG) Log.d("InterstitialAdState", "Failed to load: $adError")
                    FirebaseCrashlytics.getInstance().apply {
                        setCustomKey("ad_location", adLocation)
                        setCustomKey("ad_error_code", adError.code)
                        recordException(Exception("InterstitialAd load failed: ${adError.message}"))
                    }
                    Analytics.analyticsAdFailedToLoad(
                        Analytics.AD_TYPE_INTERSTITIAL, adLocation, adError.message
                    )
                    interstitialAd = null
                    isLoading = false

                    if (retryCount < maxRetries) {
                        retryCount++
                        load()
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    if (BuildConfig.DEBUG) Log.d("InterstitialAdState", "Ad was loaded for $adLocation")
                    interstitialAd = ad
                    isLoading = false
                    retryCount = 0
                }
            }
        )
    }

    override fun show(onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    Analytics.analyticsAdImpression(Analytics.AD_TYPE_INTERSTITIAL, adLocation)
                }

                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    load()
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    if (BuildConfig.DEBUG) Log.d("InterstitialAdState", "Failed to show: $adError")
                    interstitialAd = null
                    load()
                    onAdDismissed()
                }
            }
            ad.show(activity)
        } else {
            if (BuildConfig.DEBUG) Log.d("InterstitialAdState", "The interstitial ad wasn't ready yet.")
            load()
            onAdDismissed()
        }
    }
}

@Composable
actual fun rememberInterstitialAdState(adUnitId: String, adLocation: String): InterstitialAdState {
    val context = LocalContext.current
    val activity = context as Activity
    return remember(adUnitId) {
        AndroidInterstitialAdState(activity, adUnitId, adLocation).also { it.load() }
    }
}
