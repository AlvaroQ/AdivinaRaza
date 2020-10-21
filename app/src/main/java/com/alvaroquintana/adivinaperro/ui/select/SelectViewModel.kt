package com.alvaroquintana.adivinaperro.ui.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinaperro.common.ScopedViewModel

class SelectViewModel : ScopedViewModel() {

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    fun navigateToGame() {
        _navigation.value = Navigation.Game
    }

    sealed class Navigation {
        object Game : Navigation()
    }
}