package com.alvaroquintana.adivinaperro.ui.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinaperro.common.ScopedViewModel
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.domain.User
import com.alvaroquintana.usecases.GetRankingScore
import kotlinx.coroutines.launch

class RankingViewModel(private val getRankingScore: GetRankingScore) : ScopedViewModel() {

    private val _progress = MutableLiveData<UiModel>()
    val progress: LiveData<UiModel> = _progress

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    private val _rankingList = MutableLiveData<MutableList<User>>()
    val rankingList: LiveData<MutableList<User>> = _rankingList

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_RANKING)
        launch {
            _progress.value = UiModel.Loading(true)
            _rankingList.value = getRanking()
            _progress.value = UiModel.Loading(false)
        }
    }

    private suspend fun getRanking(): MutableList<User> {
        return getRankingScore.invoke()
    }

    sealed class Navigation {
        object Result : Navigation()
    }

    sealed class UiModel {
        data class Loading(val show: Boolean) : UiModel()
    }
}