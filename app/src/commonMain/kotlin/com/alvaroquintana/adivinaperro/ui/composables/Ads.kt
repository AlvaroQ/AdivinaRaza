package com.alvaroquintana.adivinaperro.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface InterstitialAdState {
    fun show(onAdDismissed: () -> Unit = {})
}

interface RewardedAdState {
    fun show()
}

@Composable
expect fun rememberInterstitialAdState(adUnitId: String, adLocation: String): InterstitialAdState

@Composable
expect fun rememberRewardedAdState(adUnitId: String, adLocation: String): RewardedAdState

@Composable
expect fun AdBannerView(
    adUnitId: String,
    modifier: Modifier = Modifier,
    adLocation: String
)
