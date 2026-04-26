package com.alvaroquintana.adivinaperro.managers

import android.content.Context
import android.os.Bundle
import com.alvaroquintana.adivinaperro.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

actual object Analytics {
    private lateinit var mFirebase: FirebaseAnalytics

    fun initialize(ctx: Context) {
        mFirebase = FirebaseAnalytics.getInstance(ctx.applicationContext)
    }

    actual val SCREEN_GAME: String = "screen_game"
    actual val SCREEN_RESULT: String = "screen_result"
    actual val SCREEN_SELECT_GAME: String = "screen_select_game"
    actual val SCREEN_DESCRIPTION_GAME: String = "screen_description_game"
    actual val SCREEN_BIGGER_SMALLER: String = "screen_bigger_smaller"
    actual val SCREEN_FCI_TRIVIA: String = "screen_fci_trivia"
    actual val SCREEN_CARE_FOOD: String = "screen_care_food"
    actual val SCREEN_INFO: String = "screen_info"
    actual val SCREEN_SETTINGS: String = "screen_settings"

    actual val MODE_CLASSIC: String = "classic"
    actual val MODE_BIGGER_SMALLER: String = "bigger_smaller"
    actual val MODE_DESCRIPTION: String = "description"
    actual val MODE_FCI_TRIVIA: String = "fci_trivia"
    actual val MODE_CARE_FOOD: String = "care_food"

    actual val AD_TYPE_BANNER: String = "banner"
    actual val AD_TYPE_REWARDED: String = "rewarded"
    actual val AD_TYPE_INTERSTITIAL: String = "interstitial"

    actual val AD_LOC_GAME: String = "game"
    actual val AD_LOC_GAME_OVER: String = "game_over"
    actual val AD_LOC_INFO: String = "info"

    actual val BTN_PLAY_AGAIN: String = "btn_play_again"
    actual val BTN_RATE: String = "btn_rate"
    actual val BTN_SHARE: String = "btn_share"
    actual val BTN_LEARN: String = "btn_learn"
    actual val BTN_SETTINGS: String = "btn_settings"

    actual fun analyticsScreenViewed(screenTitle: String) {
        logEvent(Event("screen_viewed")
            .with("screen_title", screenTitle)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    actual fun analyticsGameFinished(points: String, gameMode: String) {
        logEvent(Event("game_finished")
            .with("points", points)
            .with("game_mode", gameMode)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    actual fun analyticsGameModeSelected(gameMode: String) {
        logEvent(Event("game_mode_selected")
            .with("game_mode", gameMode)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    actual fun analyticsGameAnswer(isCorrect: Boolean, stage: Int, gameMode: String) {
        logEvent(Event("game_answer")
            .with("is_correct", isCorrect.toString())
            .with("stage", stage.toString())
            .with("game_mode", gameMode)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    actual fun analyticsAdImpression(adType: String, adLocation: String) {
        logEvent(Event("ad_impression")
            .with("ad_type", adType)
            .with("ad_location", adLocation)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    actual fun analyticsAdRewardEarned(adLocation: String) {
        logEvent(Event("ad_reward_earned")
            .with("ad_location", adLocation)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    actual fun analyticsAdFailedToLoad(adType: String, adLocation: String, errorMessage: String) {
        logEvent(Event("ad_failed_to_load")
            .with("ad_type", adType)
            .with("ad_location", adLocation)
            .with("error_message", errorMessage)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    actual fun analyticsClicked(btnDescription: String) {
        logEvent(Event("clicked")
            .with("component", btnDescription)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    actual fun analyticsAppRecommendedOpen(appName: String) {
        logEvent(Event("app_recommended_open")
            .with("recommended_app", appName)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    actual fun setUserPropertyGameMode(gameMode: String) {
        mFirebase.setUserProperty("favorite_game_mode", gameMode)
    }

    actual fun setUserPropertyTotalGames(totalGames: Int) {
        mFirebase.setUserProperty("total_games_played", totalGames.toString())
    }

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
}
