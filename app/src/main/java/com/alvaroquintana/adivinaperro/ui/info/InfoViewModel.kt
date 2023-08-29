package com.alvaroquintana.adivinaperro.ui.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinaperro.common.ScopedViewModel
import com.alvaroquintana.adivinaperro.ui.game.GameViewModel
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetBreedList
import kotlinx.coroutines.launch

class InfoViewModel(private val getBreedList: GetBreedList) : ScopedViewModel() {
    private var list = mutableListOf<Dog>()

    private val _progress = MutableLiveData<UiModel>()
    val progress: LiveData<UiModel> = _progress

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    private val _dogList = MutableLiveData<MutableList<Dog>>()
    val dogList: LiveData<MutableList<Dog>> = _dogList

    private val _updateDogList = MutableLiveData<MutableList<Dog>>()
    val updateDogList: LiveData<MutableList<Dog>> = _updateDogList

    private val _showingAds = MutableLiveData<UiModel>()
    val showingAds: LiveData<UiModel> = _showingAds

    init {
        launch {
            _progress.value = UiModel.Loading(true)
            _dogList.value = getBreedList(0)
            _showingAds.value = UiModel.ShowAd(true)
            _progress.value = UiModel.Loading(false)
        }
    }

    fun loadMoreDogList(currentPage: Int) {
        launch {
            _progress.value = UiModel.Loading(true)
            _updateDogList.value = getBreedList(currentPage)
            _progress.value = UiModel.Loading(false)
        }
    }

    private suspend fun getBreedList(currentPage: Int): MutableList<Dog> {
        list = (list + getBreedList.invoke(currentPage)) as MutableList<Dog>
        return list
    }

    fun navigateToSelect() {
        _navigation.value = Navigation.Select
    }

    fun showRewardedAd() {
        _showingAds.value = UiModel.ShowReewardAd(true)
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