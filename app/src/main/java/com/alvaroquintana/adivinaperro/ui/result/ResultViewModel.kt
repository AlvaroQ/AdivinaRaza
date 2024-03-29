package com.alvaroquintana.adivinaperro.ui.result

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.alvaroquintana.adivinaperro.common.ScopedViewModel
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.adivinaperro.utils.Constants.RECORD_PERSONAL
import com.alvaroquintana.domain.App
import com.alvaroquintana.domain.User
import com.alvaroquintana.usecases.GetAppsRecommended
import com.alvaroquintana.usecases.GetRecordScore
import com.alvaroquintana.usecases.SaveTopScore
import kotlinx.coroutines.launch

class ResultViewModel(private val getAppsRecommended: GetAppsRecommended,
                      private val saveTopScore: SaveTopScore,
                      private val getRecordScore: GetRecordScore
) : ScopedViewModel() {

    private val _progress = MutableLiveData<UiModel>()
    val progress: LiveData<UiModel> = _progress

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    private val _list = MutableLiveData<MutableList<App>>()
    val list: LiveData<MutableList<App>> = _list

    private val _personalRecord = MutableLiveData<String>()
    val personalRecord: LiveData<String> = _personalRecord

    private val _worldRecord = MutableLiveData<String>()
    val worldRecord: LiveData<String> = _worldRecord

    private val _showingAds = MutableLiveData<UiModel>()
    val showingAds: LiveData<UiModel> = _showingAds

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_RESULT)
        launch {
            _progress.value = UiModel.Loading(true)
            _list.value = appsRecommended()
            _worldRecord.value = getPointsWorldRecord()
            _showingAds.value = UiModel.ShowAd(true)
            _progress.value = UiModel.Loading(false)
        }
    }

    private suspend fun appsRecommended(): MutableList<App> {
        return getAppsRecommended.invoke()
    }

    private suspend fun getPointsWorldRecord(): String {
        return getRecordScore.invoke(1)
    }

    fun setPersonalRecordOnServer(gamePoints: Int) {
        launch {
            val pointsLastClassified = getRecordScore.invoke(30)
            if(gamePoints > pointsLastClassified.toInt()) {
                showDialogToSaveGame(gamePoints.toString())
            }
        }
    }

    fun saveTopScore(user: User) {
        launch {
            saveTopScore.invoke(user)
        }
    }

    fun getPersonalRecord(points: Int, context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val personalRecordPoints = sharedPreferences.getInt(RECORD_PERSONAL, 0)
        if(points > personalRecordPoints) {
            savePersonalRecord(context, personalRecordPoints)
            _personalRecord.value = points.toString()
        } else {
            _personalRecord.value = personalRecordPoints.toString()
        }
    }

    private fun savePersonalRecord(context: Context, record: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putInt(RECORD_PERSONAL, record).apply()
    }

    private fun showDialogToSaveGame(points: String) {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_DIALOG_SAVE_SCORE)
        _navigation.value = Navigation.Dialog(points)
    }

    fun onAppClicked(url: String) {
        Analytics.analyticsAppRecommendedOpen(url)
        _navigation.value = Navigation.Open(url)
    }

    fun navigateToGame() {
        Analytics.analyticsClicked(Analytics.BTN_PLAY_AGAIN)
        _navigation.value = Navigation.Game
    }

    fun navigateToRate() {
        Analytics.analyticsClicked(Analytics.BTN_RATE)
        _navigation.value = Navigation.Rate
    }

    fun navigateToRanking() {
        Analytics.analyticsClicked(Analytics.BTN_RANKING)
        _navigation.value = Navigation.Ranking
    }

    fun navigateToShare(points: Int) {
        Analytics.analyticsClicked(Analytics.BTN_SHARE)
        _navigation.value = Navigation.Share(points)
    }

    sealed class Navigation {
        data class Share(val points: Int) : Navigation()
        object Rate : Navigation()
        object Game : Navigation()
        object Ranking : Navigation()
        data class Dialog(val points : String): Navigation()
        data class Open(val url : String): Navigation()
    }

    sealed class UiModel {
        data class Loading(val show: Boolean) : UiModel()
        data class ShowAd(val show: Boolean) : UiModel()
    }
}