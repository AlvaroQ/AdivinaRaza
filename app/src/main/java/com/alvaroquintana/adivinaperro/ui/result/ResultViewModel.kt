package com.alvaroquintana.adivinaperro.ui.result

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.adivinaperro.utils.Constants.RECORD_PERSONAL
import com.alvaroquintana.domain.App
import com.alvaroquintana.domain.User
import com.alvaroquintana.usecases.GetAppsRecommended
import com.alvaroquintana.usecases.GetRecordScore
import com.alvaroquintana.usecases.SaveTopScore
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultViewModel(private val getAppsRecommended: GetAppsRecommended,
                      private val saveTopScore: SaveTopScore,
                      private val getRecordScore: GetRecordScore
) : ViewModel() {

    private val _progress = MutableStateFlow<UiModel>(UiModel.Loading(false))
    val progress: StateFlow<UiModel> = _progress.asStateFlow()

    private val _navigation = MutableSharedFlow<Navigation>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val navigation: SharedFlow<Navigation> = _navigation.asSharedFlow()

    private val _list = MutableStateFlow<MutableList<App>>(mutableListOf())
    val list: StateFlow<MutableList<App>> = _list.asStateFlow()

    private val _personalRecord = MutableStateFlow("")
    val personalRecord: StateFlow<String> = _personalRecord.asStateFlow()

    private val _worldRecord = MutableStateFlow("")
    val worldRecord: StateFlow<String> = _worldRecord.asStateFlow()

    private val _showingAds = MutableSharedFlow<UiModel>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val showingAds: SharedFlow<UiModel> = _showingAds.asSharedFlow()

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_RESULT)
        viewModelScope.launch {
            _progress.value = UiModel.Loading(true)
            _list.value = appsRecommended()
            _worldRecord.value = getPointsWorldRecord()
            _showingAds.tryEmit(UiModel.ShowAd(true))
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
        viewModelScope.launch {
            val pointsLastClassified = getRecordScore.invoke(30)
            if(gamePoints > pointsLastClassified.toInt()) {
                showDialogToSaveGame(gamePoints.toString())
            }
        }
    }

    fun saveTopScore(user: User) {
        viewModelScope.launch {
            saveTopScore.invoke(user)
            Analytics.analyticsRecordSaved(user.points ?: 0, user.name ?: "")
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
        _navigation.tryEmit(Navigation.Dialog(points))
    }

    fun onAppClicked(url: String) {
        Analytics.analyticsAppRecommendedOpen(url)
        _navigation.tryEmit(Navigation.Open(url))
    }

    fun navigateToGame() {
        Analytics.analyticsClicked(Analytics.BTN_PLAY_AGAIN)
        _navigation.tryEmit(Navigation.Game)
    }

    fun navigateToRate() {
        Analytics.analyticsClicked(Analytics.BTN_RATE)
        _navigation.tryEmit(Navigation.Rate)
    }

    fun navigateToRanking() {
        Analytics.analyticsClicked(Analytics.BTN_RANKING)
        _navigation.tryEmit(Navigation.Ranking)
    }

    fun navigateToShare(points: Int) {
        Analytics.analyticsClicked(Analytics.BTN_SHARE)
        _navigation.tryEmit(Navigation.Share(points))
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