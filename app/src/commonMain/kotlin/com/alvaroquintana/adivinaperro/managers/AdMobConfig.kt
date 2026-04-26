package com.alvaroquintana.adivinaperro.managers

/**
 * AdMob unit IDs consumed by the multiplatform navigation host.
 * Resolved at the Android entry point from build.gradle resValues, and
 * stubbed (empty strings) on iOS until phase 6c wires up Google Mobile
 * Ads iOS via cinterop.
 */
data class AdMobConfig(
    val bannerGame: String,
    val bannerInfo: String,
    val bonificadoGame: String,
    val interstitialGameOver: String
) {
    companion object {
        val EMPTY = AdMobConfig("", "", "", "")
    }
}
