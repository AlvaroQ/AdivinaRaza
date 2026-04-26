package com.alvaroquintana.adivinaperro.managers

import android.content.Context
import android.os.Bundle
import com.alvaroquintana.adivinaperro.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

object Analytics {
    lateinit var mFirebase: FirebaseAnalytics

    fun initialize(ctx: Context) {
        mFirebase = FirebaseAnalytics.getInstance(ctx.applicationContext)
    }

    // region Screen Events

    fun analyticsScreenViewed(screenTitle: String) {
        logEvent(Event("screen_viewed")
            .with("screen_title", screenTitle)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    // endregion

    // region Game Events

    fun analyticsGameFinished(points: String, gameMode: String) {
        logEvent(Event("game_finished")
            .with("points", points)
            .with("game_mode", gameMode)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    fun analyticsGameModeSelected(gameMode: String) {
        logEvent(Event("game_mode_selected")
            .with("game_mode", gameMode)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    fun analyticsGameAnswer(isCorrect: Boolean, stage: Int, gameMode: String) {
        logEvent(Event("game_answer")
            .with("is_correct", isCorrect.toString())
            .with("stage", stage.toString())
            .with("game_mode", gameMode)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    // endregion

    // region Ad Events

    fun analyticsAdImpression(adType: String, adLocation: String) {
        logEvent(Event("ad_impression")
            .with("ad_type", adType)
            .with("ad_location", adLocation)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    fun analyticsAdRewardEarned(adLocation: String) {
        logEvent(Event("ad_reward_earned")
            .with("ad_location", adLocation)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    fun analyticsAdFailedToLoad(adType: String, adLocation: String, errorMessage: String) {
        logEvent(Event("ad_failed_to_load")
            .with("ad_type", adType)
            .with("ad_location", adLocation)
            .with("error_message", errorMessage)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    // endregion

    // region Click Events

    fun analyticsClicked(btnDescription: String) {
        logEvent(Event("clicked")
            .with("component", btnDescription)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    fun analyticsAppRecommendedOpen(appName: String) {
        logEvent(Event("app_recommended_open")
            .with("recommended_app", appName)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    // endregion

    // region User Properties

    fun setUserPropertyGameMode(gameMode: String) {
        mFirebase.setUserProperty("favorite_game_mode", gameMode)
    }

    fun setUserPropertyTotalGames(totalGames: Int) {
        mFirebase.setUserProperty("total_games_played", totalGames.toString())
    }

    // endregion

    private fun logEvent(event: Event) {
        mFirebase.logEvent(event.eventName, event.bundle)
    }

    private class Event(val eventName: String) {
        val bundle = Bundle()
        fun with(key: String, value: String): Event {
            bundle.putString(key, value)
            return this
        }
    }

    // Screens
    const val SCREEN_GAME = "screen_game"
    const val SCREEN_RESULT = "screen_result"
    const val SCREEN_SELECT_GAME = "screen_select_game"
    const val SCREEN_DESCRIPTION_GAME = "screen_description_game"
    const val SCREEN_BIGGER_SMALLER = "screen_bigger_smaller"
    const val SCREEN_FCI_TRIVIA = "screen_fci_trivia"
    const val SCREEN_CARE_FOOD = "screen_care_food"
    const val SCREEN_INFO = "screen_info"
    const val SCREEN_SETTINGS = "screen_settings"

    // Game Modes
    const val MODE_CLASSIC = "classic"
    const val MODE_BIGGER_SMALLER = "bigger_smaller"
    const val MODE_DESCRIPTION = "description"
    const val MODE_FCI_TRIVIA = "fci_trivia"
    const val MODE_CARE_FOOD = "care_food"

    // Ad Types
    const val AD_TYPE_BANNER = "banner"
    const val AD_TYPE_REWARDED = "rewarded"
    const val AD_TYPE_INTERSTITIAL = "interstitial"

    // Ad Locations
    const val AD_LOC_GAME = "game"
    const val AD_LOC_GAME_OVER = "game_over"
    const val AD_LOC_INFO = "info"

    // Clicked
    const val BTN_PLAY_AGAIN = "btn_play_again"
    const val BTN_RATE = "btn_rate"
    const val BTN_SHARE = "btn_share"
    const val BTN_LEARN = "btn_learn"
    const val BTN_SETTINGS = "btn_settings"
}
