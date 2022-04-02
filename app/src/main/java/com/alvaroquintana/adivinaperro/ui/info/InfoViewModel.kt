package com.alvaroquintana.adivinaperro.ui.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinaperro.common.ScopedViewModel
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetBreedList
import kotlinx.coroutines.launch

class InfoViewModel(private val getBreedList: GetBreedList) : ScopedViewModel() {
    private var list = mutableListOf<Dog>()

    private val _progress = MutableLiveData<UiModel>()
    val progress: LiveData<UiModel> = _progress

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    private val _prideList = MutableLiveData<MutableList<Dog>>()
    val prideList: LiveData<MutableList<Dog>> = _prideList

    private val _updatePrideList = MutableLiveData<MutableList<Dog>>()
    val updatePrideList: LiveData<MutableList<Dog>> = _updatePrideList

    private val _showingAds = MutableLiveData<UiModel>()
    val showingAds: LiveData<UiModel> = _showingAds

    init {
        launch {
            _progress.value = UiModel.Loading(true)
            _prideList.value = getBreedList(0)
            _showingAds.value = UiModel.ShowAd(true)
            _progress.value = UiModel.Loading(false)
        }
    }

    fun loadMorePrideList(currentPage: Int) {
        launch {
            _progress.value = UiModel.Loading(true)
            _updatePrideList.value = getBreedList(currentPage)
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

    sealed class Navigation {
        object Select : Navigation()
    }

    sealed class UiModel {
        data class Loading(val show: Boolean) : UiModel()
        data class ShowAd(val show: Boolean) : UiModel()
    }
}