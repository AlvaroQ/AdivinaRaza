package com.alvaroquintana.adivinaperro.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinaperro.common.ScopedViewModel
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_BREED
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetBreedById
import kotlinx.coroutines.launch

class GameViewModel(private val getBreedById: GetBreedById) : ScopedViewModel() {
    private var randomBreeds = mutableListOf<Int>()
    private lateinit var dog: Dog

    private val _question = MutableLiveData<String>()
    val question: LiveData<String> = _question

    private val _responseOptions = MutableLiveData<MutableList<String>>()
    val responseOptions: LiveData<MutableList<String>> = _responseOptions

    private val _progress = MutableLiveData<UiModel>()
    val progress: LiveData<UiModel> = _progress

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    private val _showingAds = MutableLiveData<UiModel>()
    val showingAds: LiveData<UiModel> = _showingAds

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_GAME)
        generateNewStage()
        _showingAds.value = UiModel.ShowBannerAd(true)
    }

    fun generateNewStage() {
        launch {
            _progress.value = UiModel.Loading(true)

            /** Generate question */
            val numRandomMain = generateRandomWithExcusion(0, TOTAL_BREED, *randomBreeds.toIntArray())
            randomBreeds.add(numRandomMain)

            dog = getBreed(numRandomMain)

            /** Generate responses */
            val numRandomMainPosition = generateRandomWithExcusion(0, 3)

            val numRandomOption1 = generateRandomWithExcusion(1, TOTAL_BREED, numRandomMain)
            val dogOption1: Dog = getBreed(numRandomOption1)
            val numRandomPosition1 = generateRandomWithExcusion(0, 3, numRandomMainPosition)

            val numRandomOption2 = generateRandomWithExcusion(1, TOTAL_BREED, numRandomMain, numRandomOption1)
            val dogOption2: Dog = getBreed(numRandomOption2)
            val numRandomPosition2 = generateRandomWithExcusion(0, 3, numRandomMainPosition, numRandomPosition1)

            val numRandomOption3 = generateRandomWithExcusion(1, TOTAL_BREED, numRandomMain, numRandomOption1, numRandomOption2)
            val dogOption3: Dog = getBreed(numRandomOption3)
            val numRandomPosition3 = generateRandomWithExcusion(0, 3, numRandomMainPosition, numRandomPosition1, numRandomPosition2)

            /** Save value */
            val optionList = mutableListOf("", "", "", "")
            optionList[numRandomMainPosition] = dog.name!!
            optionList[numRandomPosition1] = dogOption1.name!!
            optionList[numRandomPosition2] = dogOption2.name!!
            optionList[numRandomPosition3] = dogOption3.name!!

            _responseOptions.value = optionList
            _question.value = dog.icon
            _progress.value = UiModel.Loading(false)
        }
    }

    fun showRewardedAd() {
        _showingAds.value = UiModel.ShowReewardAd(true)
    }

    private suspend fun getBreed(id: Int): Dog {
        return getBreedById.invoke(id)
    }

    fun navigateToResult(points: String) {
        Analytics.analyticsGameFinished(points)
        _navigation.value = Navigation.Result
    }

    fun getNameBreedCorrect() : String? {
        return dog.name
    }

    private fun generateRandomWithExcusion(start: Int, end: Int, vararg exclude: Int): Int {
        var numRandom = (start..end).random()
        while(exclude.contains(numRandom)){
            numRandom = (start..end).random()
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