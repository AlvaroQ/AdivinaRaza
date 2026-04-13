package com.alvaroquintana.adivinaperro.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_BREED
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetBreedById
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val getBreedById: GetBreedById) : ViewModel() {
    private var randomBreeds = mutableListOf<Int>()
    private lateinit var dog: Dog

    private val _question = MutableStateFlow("")
    val question: StateFlow<String> = _question.asStateFlow()

    private val _responseOptions = MutableSharedFlow<MutableList<String>>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val responseOptions: SharedFlow<MutableList<String>> = _responseOptions.asSharedFlow()

    private val _progress = MutableStateFlow<UiModel>(UiModel.Loading(false))
    val progress: StateFlow<UiModel> = _progress.asStateFlow()

    private val _navigation = MutableSharedFlow<Navigation>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val navigation: SharedFlow<Navigation> = _navigation.asSharedFlow()

    private val _showingAds = MutableSharedFlow<UiModel>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val showingAds: SharedFlow<UiModel> = _showingAds.asSharedFlow()

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_GAME)
        generateNewStage()
        _showingAds.tryEmit(UiModel.ShowBannerAd(true))
    }

    fun generateNewStage() {
        viewModelScope.launch {
            _progress.value = UiModel.Loading(true)

            /** Generate question */
            val numRandomMain = generateRandomExcluding(0, TOTAL_BREED, *randomBreeds.toIntArray())
            randomBreeds.add(numRandomMain)

            dog = getBreed(numRandomMain)

            /** Generate responses */
            val numRandomMainPosition = generateRandomExcluding(0, 4)

            val numRandomOption1 = generateRandomExcluding(0, TOTAL_BREED, numRandomMain)
            val dogOption1: Dog = getBreed(numRandomOption1)
            val numRandomPosition1 = generateRandomExcluding(0, 4, numRandomMainPosition)

            val numRandomOption2 = generateRandomExcluding(0, TOTAL_BREED, numRandomMain, numRandomOption1)
            val dogOption2: Dog = getBreed(numRandomOption2)
            val numRandomPosition2 = generateRandomExcluding(0, 4, numRandomMainPosition, numRandomPosition1)

            val numRandomOption3 = generateRandomExcluding(0, TOTAL_BREED, numRandomMain, numRandomOption1, numRandomOption2)
            val dogOption3: Dog = getBreed(numRandomOption3)
            val numRandomPosition3 = generateRandomExcluding(0, 4, numRandomMainPosition, numRandomPosition1, numRandomPosition2)

            /** Save value */
            val optionList = mutableListOf("", "", "", "")
            optionList[numRandomMainPosition] = dog.name
            optionList[numRandomPosition1] = dogOption1.name
            optionList[numRandomPosition2] = dogOption2.name
            optionList[numRandomPosition3] = dogOption3.name

            _responseOptions.tryEmit(optionList)
            _question.value = dog.icon
            _progress.value = UiModel.Loading(false)

        }
    }

    fun showRewardedAd() {
        _showingAds.tryEmit(UiModel.ShowReewardAd(true))
    }

    private suspend fun getBreed(id: Int): Dog {
        return getBreedById.invoke(id)
    }

    fun navigateToResult(points: String) {
        Analytics.analyticsGameFinished(points, Analytics.MODE_CLASSIC)
        _navigation.tryEmit(Navigation.Result)
    }

    fun getNameBreedCorrect() : String {
        return dog.name
    }

    private fun generateRandomExcluding(startInclusive: Int, endExclusive: Int, vararg exclude: Int): Int {
        var numRandom = (startInclusive until endExclusive).random()
        while(exclude.contains(numRandom)){
            numRandom = (startInclusive until endExclusive).random()
        }
        return numRandom
    }

    sealed class UiModel {
        data class Loading(val show: Boolean) : UiModel()
        data class ShowBannerAd(val show: Boolean) : UiModel()
        data class ShowReewardAd(val show: Boolean) : UiModel()
    }

    sealed class Navigation {
        object Result : Navigation()
    }
}