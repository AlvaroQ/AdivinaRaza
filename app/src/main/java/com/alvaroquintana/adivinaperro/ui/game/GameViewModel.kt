package com.alvaroquintana.adivinaperro.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinaperro.common.ScopedViewModel
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetBreedById
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class GameViewModel(private val getBreedById: GetBreedById) : ScopedViewModel() {
    private var randomBreeds = mutableListOf<Int>()

    private val _question = MutableLiveData<String>()
    val question: LiveData<String> = _question

    private val _responseOptions = MutableLiveData<MutableList<String>>()
    val responseOptions: LiveData<MutableList<String>> = _responseOptions

    private val _progress = MutableLiveData<UiModel>()
    val progress: LiveData<UiModel> = _progress

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    init {
        generateGame()
    }

    fun generateGame() {
        launch {
            _progress.value = UiModel.Loading(true)

            /** Generate question */
            val numRandomMain = generateRandomWithExcusion(0, TOTAL_BREED, *randomBreeds.toIntArray())
            randomBreeds.add(numRandomMain)
            val dog: Dog = getBreed(numRandomMain)

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

    private suspend fun getBreed(id: Int): Dog {
        return getBreedById.invoke(id)
    }

    fun navigateToGame() {
        _navigation.value = Navigation.Game
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
        object StartGame : UiModel()
    }

    sealed class Navigation {
        object Game : Navigation()
    }

    companion object {
        const val TOTAL_BREED = 319
    }
}