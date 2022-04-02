package com.alvaroquintana.adivinaperro.ui.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinaperro.common.ScopedViewModel
import com.alvaroquintana.adivinaperro.managers.Analytics

class SelectViewModel : ScopedViewModel() {

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_SELECT_GAME)
    }

    fun navigateToGame() {
        _navigation.value = Navigation.Game
    }

    fun navigateToLearn() {
        _navigation.value = Navigation.Learn
    }

    fun navigateToSettings() {
        _navigation.value = Navigation.Setting
    }

    sealed class Navigation {
        object Game : Navigation()
        object Learn : Navigation()
        object Setting : Navigation()
    }
}