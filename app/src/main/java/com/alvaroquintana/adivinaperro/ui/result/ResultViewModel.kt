package com.alvaroquintana.adivinaperro.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinaperro.common.ScopedViewModel
import com.alvaroquintana.domain.App
import com.alvaroquintana.usecases.GetAppsRecommended
import kotlinx.coroutines.launch

class ResultViewModel(private val getAppsRecommended: GetAppsRecommended) : ScopedViewModel() {

    private val _progress = MutableLiveData<UiModel>()
    val progress: LiveData<UiModel> = _progress

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    private val _list = MutableLiveData<MutableList<App>>()
    val list: LiveData<MutableList<App>> = _list

    init {
        launch {
            _progress.value = UiModel.Loading(true)
            _list.value = appsRecommended()
            _progress.value = UiModel.Loading(false)
        }
    }

    private suspend fun appsRecommended(): MutableList<App> {
        return getAppsRecommended.invoke()
    }

    fun onAppClicked(url: String) {
        _navigation.value = Navigation.Open(url)
    }

    fun navigateToGame() {
        _navigation.value = Navigation.Game
    }

    sealed class Navigation {
        object Game : Navigation()
        data class Open(val url : String): Navigation()
    }

    sealed class UiModel {
        data class Loading(val show: Boolean) : UiModel()
    }
}