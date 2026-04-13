package com.alvaroquintana.adivinaperro.ui.select

import androidx.lifecycle.ViewModel
import com.alvaroquintana.adivinaperro.managers.Analytics
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SelectViewModel : ViewModel() {

    private val _navigation = MutableSharedFlow<Navigation>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val navigation: SharedFlow<Navigation> = _navigation.asSharedFlow()

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_SELECT_GAME)
    }

    fun navigateToGame() {
        _navigation.tryEmit(Navigation.Game)
    }

    fun navigateToBiggerSmaller() {
        _navigation.tryEmit(Navigation.BiggerSmaller)
    }

    fun navigateToDescription() {
        _navigation.tryEmit(Navigation.Description)
    }

    fun navigateToLearn() {
        _navigation.tryEmit(Navigation.Learn)
    }

    fun navigateToSettings() {
        _navigation.tryEmit(Navigation.Setting)
    }

    sealed class Navigation {
        object Game : Navigation()
        object BiggerSmaller : Navigation()
        object Description : Navigation()
        object Learn : Navigation()
        object Setting : Navigation()
    }
}