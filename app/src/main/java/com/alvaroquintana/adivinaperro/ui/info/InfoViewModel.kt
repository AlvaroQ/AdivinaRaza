package com.alvaroquintana.adivinaperro.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_ITEM_EACH_LOAD
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
    private val list = mutableListOf<Dog>()
    private var viewedItemsCount = 0
    private var shouldShowRewardedAdOnClose = false

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _progress = MutableStateFlow<UiModel>(UiModel.Loading(false))
    val progress: StateFlow<UiModel> = _progress.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedDog = MutableStateFlow<Dog?>(null)
    val selectedDog: StateFlow<Dog?> = _selectedDog.asStateFlow()

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
        loadInitialDogList()
    }

    private fun loadInitialDogList() {
        viewModelScope.launch {
            _progress.value = UiModel.Loading(true)
            _errorMessage.value = null
            runCatching {
                val initialList = fetchPage(0, reset = true)
                _dogList.tryEmit(initialList.toMutableList())
                _currentDogList.value = initialList
                _hasMore.value = initialList.size >= TOTAL_ITEM_EACH_LOAD
                _showingAds.tryEmit(UiModel.ShowAd(true))
            }.onFailure {
                _errorMessage.value = it.message
            }
            _progress.value = UiModel.Loading(false)
        }
    }

    fun loadMoreDogList(currentPage: Int) {
        viewModelScope.launch {
            _progress.value = UiModel.Loading(true)
            _errorMessage.value = null
            runCatching {
                val updatedList = fetchPage(currentPage, reset = false)
                _updateDogList.tryEmit(updatedList.toMutableList())
                _currentDogList.value = updatedList
            }.onFailure {
                _errorMessage.value = it.message
            }
            _progress.value = UiModel.Loading(false)
        }
    }

    private suspend fun fetchPage(currentPage: Int, reset: Boolean): List<Dog> {
        if (reset) {
            list.clear()
            _hasMore.value = true
        }
        val page = getBreedList.invoke(currentPage)
        if (page.isEmpty() || page.size < TOTAL_ITEM_EACH_LOAD) {
            _hasMore.value = false
        }
        list.addAll(page)
        return list.toList()
    }

    fun retryInitialLoad() {
        if (_progress.value is UiModel.Loading && (_progress.value as UiModel.Loading).show) return
        loadInitialDogList()
    }

    fun selectDog(dog: Dog) {
        viewedItemsCount += 1
        shouldShowRewardedAdOnClose = viewedItemsCount % REWARDED_AD_EVERY_VIEWED_ITEMS == 0
        _selectedDog.value = dog
    }

    fun closeDogDetail() {
        val wasShowingDetail = _selectedDog.value != null
        _selectedDog.value = null
        if (wasShowingDetail && shouldShowRewardedAdOnClose) {
            _showingAds.tryEmit(UiModel.ShowReewardAd(true))
            shouldShowRewardedAdOnClose = false
        }
    }

    fun navigateToSelect() {
        _navigation.tryEmit(Navigation.Select)
    }

    sealed class Navigation {
        object Select : Navigation()
    }

    companion object {
        private const val REWARDED_AD_EVERY_VIEWED_ITEMS = 5
    }

    sealed class UiModel {
        data class Loading(val show: Boolean) : UiModel()
        data class ShowAd(val show: Boolean) : UiModel()
        data class ShowReewardAd(val show: Boolean) : UiModel()
    }
}