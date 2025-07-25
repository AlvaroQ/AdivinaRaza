package com.alvaroquintana.adivinaperro.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetBreedList
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InfoViewModel(private val getBreedList: GetBreedList) : ViewModel() {
    private var list = mutableListOf<Dog>()

    private val _progress = MutableStateFlow<UiModel>(UiModel.Loading(false))
    val progress: StateFlow<UiModel> = _progress.asStateFlow()

    private val _navigation = MutableSharedFlow<Navigation>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val navigation: SharedFlow<Navigation> = _navigation.asSharedFlow()

    private val _dogList = MutableSharedFlow<MutableList<Dog>>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val dogList: SharedFlow<MutableList<Dog>> = _dogList.asSharedFlow()

    private val _updateDogList = MutableSharedFlow<MutableList<Dog>>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val updateDogList: SharedFlow<MutableList<Dog>> = _updateDogList.asSharedFlow()

    private val _currentDogList = MutableStateFlow<List<Dog>>(emptyList())
    val currentDogList: StateFlow<List<Dog>> = _currentDogList.asStateFlow()

    private val _showingAds = MutableSharedFlow<UiModel>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val showingAds: SharedFlow<UiModel> = _showingAds.asSharedFlow()

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_INFO)
        viewModelScope.launch {
            _progress.value = UiModel.Loading(true)
            val initialList = getBreedList(0)
            _dogList.tryEmit(initialList)
            _currentDogList.value = initialList.toList()
            _showingAds.tryEmit(UiModel.ShowAd(true))
            _progress.value = UiModel.Loading(false)
        }
    }

    fun loadMoreDogList(currentPage: Int) {
        viewModelScope.launch {
            _progress.value = UiModel.Loading(true)
            val updatedList = getBreedList(currentPage)
            _updateDogList.tryEmit(updatedList)
            _currentDogList.value = updatedList.toList()
            _progress.value = UiModel.Loading(false)
        }
    }

    private suspend fun getBreedList(currentPage: Int): MutableList<Dog> {
        list = (list + getBreedList.invoke(currentPage)) as MutableList<Dog>
        return list
    }

    fun navigateToSelect() {
        _navigation.tryEmit(Navigation.Select)
    }

    fun showRewardedAd() {
        _showingAds.tryEmit(UiModel.ShowReewardAd(true))
    }

    sealed class Navigation {
        object Select : Navigation()
    }

    sealed class UiModel {
        data class Loading(val show: Boolean) : UiModel()
        data class ShowAd(val show: Boolean) : UiModel()
        data class ShowReewardAd(val show: Boolean) : UiModel()
    }
}