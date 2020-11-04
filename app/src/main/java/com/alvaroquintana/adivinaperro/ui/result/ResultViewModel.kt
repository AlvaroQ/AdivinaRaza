package com.alvaroquintana.adivinaperro.ui.result

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.common.ResourceProvider
import com.alvaroquintana.adivinaperro.common.ScopedViewModel
import com.alvaroquintana.adivinaperro.utils.Constants
import com.alvaroquintana.adivinaperro.utils.Constants.RECORD_PERSONAL
import com.alvaroquintana.adivinaperro.utils.log
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

    init {
        launch {
            _progress.value = UiModel.Loading(true)
            _list.value = appsRecommended()
            _worldRecord.value = getPointsWorldRecord()
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
            val pointsLastClassified = getRecordScore.invoke(8)
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
        _navigation.value = Navigation.Dialog(points)
    }

    fun onAppClicked(url: String) {
        _navigation.value = Navigation.Open(url)
    }

    fun navigateToGame() {
        _navigation.value = Navigation.Game
    }

    fun navigateToRate() {
        _navigation.value = Navigation.Rate
    }

    fun navigateToRanking() {
        _navigation.value = Navigation.Ranking
    }

    fun navigateToShare(points: Int) {
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
    }
}