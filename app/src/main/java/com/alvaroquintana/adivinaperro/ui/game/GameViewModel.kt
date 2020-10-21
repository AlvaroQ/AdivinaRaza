package com.alvaroquintana.adivinaperro.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinaperro.common.ScopedViewModel

class GameViewModel : ScopedViewModel() {
    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    fun navigateToGame() {
        _navigation.value = Navigation.Game
    }

    sealed class Navigation {
        object Game : Navigation()
    }
}