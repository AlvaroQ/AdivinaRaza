package com.alvaroquintana.adivinaperro.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

private object NoOpInterstitial : InterstitialAdState {
    override fun show(onAdDismissed: () -> Unit) {
        onAdDismissed()
    }
}

private object NoOpRewarded : RewardedAdState {
    override fun show() = Unit
}

@Composable
actual fun rememberInterstitialAdState(adUnitId: String, adLocation: String): InterstitialAdState =
    remember { NoOpInterstitial }

@Composable
actual fun rememberRewardedAdState(adUnitId: String, adLocation: String): RewardedAdState =
    remember { NoOpRewarded }

@Composable
actual fun AdBannerView(
    adUnitId: String,
    modifier: Modifier,
    adLocation: String
) {
    // Banner ads on iOS will be wired to Google Mobile Ads via cinterop in phase 6.
}
