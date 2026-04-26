package com.alvaroquintana.adivinaperro.managers

actual object Analytics {
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

    actual fun analyticsScreenViewed(screenTitle: String) = Unit
    actual fun analyticsGameFinished(points: String, gameMode: String) = Unit
    actual fun analyticsGameModeSelected(gameMode: String) = Unit
    actual fun analyticsGameAnswer(isCorrect: Boolean, stage: Int, gameMode: String) = Unit
    actual fun analyticsAdImpression(adType: String, adLocation: String) = Unit
    actual fun analyticsAdRewardEarned(adLocation: String) = Unit
    actual fun analyticsAdFailedToLoad(adType: String, adLocation: String, errorMessage: String) = Unit
    actual fun analyticsClicked(btnDescription: String) = Unit
    actual fun analyticsAppRecommendedOpen(appName: String) = Unit
    actual fun setUserPropertyGameMode(gameMode: String) = Unit
    actual fun setUserPropertyTotalGames(totalGames: Int) = Unit
}
