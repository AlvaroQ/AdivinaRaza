package com.alvaroquintana.adivinaperro.managers

expect object Analytics {
    val SCREEN_GAME: String
    val SCREEN_RESULT: String
    val SCREEN_SELECT_GAME: String
    val SCREEN_DESCRIPTION_GAME: String
    val SCREEN_BIGGER_SMALLER: String
    val SCREEN_FCI_TRIVIA: String
    val SCREEN_CARE_FOOD: String
    val SCREEN_INFO: String
    val SCREEN_SETTINGS: String

    val MODE_CLASSIC: String
    val MODE_BIGGER_SMALLER: String
    val MODE_DESCRIPTION: String
    val MODE_FCI_TRIVIA: String
    val MODE_CARE_FOOD: String

    val AD_TYPE_BANNER: String
    val AD_TYPE_REWARDED: String
    val AD_TYPE_INTERSTITIAL: String

    val AD_LOC_GAME: String
    val AD_LOC_GAME_OVER: String
    val AD_LOC_INFO: String

    val BTN_PLAY_AGAIN: String
    val BTN_RATE: String
    val BTN_SHARE: String
    val BTN_LEARN: String
    val BTN_SETTINGS: String

    fun analyticsScreenViewed(screenTitle: String)
    fun analyticsGameFinished(points: String, gameMode: String)
    fun analyticsGameModeSelected(gameMode: String)
    fun analyticsGameAnswer(isCorrect: Boolean, stage: Int, gameMode: String)
    fun analyticsAdImpression(adType: String, adLocation: String)
    fun analyticsAdRewardEarned(adLocation: String)
    fun analyticsAdFailedToLoad(adType: String, adLocation: String, errorMessage: String)
    fun analyticsClicked(btnDescription: String)
    fun analyticsAppRecommendedOpen(appName: String)
    fun setUserPropertyGameMode(gameMode: String)
    fun setUserPropertyTotalGames(totalGames: Int)
}
