package com.alvaroquintana.adivinaperro.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetRandomBreedsWithWeight
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BiggerSmallerViewModel(
    private val getRandomBreedsWithWeight: GetRandomBreedsWithWeight
) : ViewModel() {

    data class GameState(
        val breedLeft: Dog? = null,
        val breedRight: Dog? = null,
        val comparisonType: ComparisonType = ComparisonType.WEIGHT,
        val score: Int = 0,
        val lives: Int = 3,
        val stage: Int = 1,
        val isLoading: Boolean = true,
        val lastResult: AnswerResult? = null
    )

    enum class ComparisonType { WEIGHT, HEIGHT }

    enum class AnswerResult { CORRECT, INCORRECT }

    sealed interface Event {
        data class NavigateToResult(val points: Int) : Event
        data class ShowBannerAd(val show: Boolean) : Event
        data class ShowRewardedAd(val show: Boolean) : Event
    }

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_BIGGER_SMALLER)
        FirebaseCrashlytics.getInstance().setCustomKey("current_screen", Analytics.SCREEN_BIGGER_SMALLER)
        loadNewRound()
        _events.tryEmit(Event.ShowBannerAd(true))
    }

    fun loadNewRound() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, lastResult = null) }

            // Alternate between weight and height every 5 rounds
            val type = if (_state.value.stage % 5 == 0) ComparisonType.HEIGHT else ComparisonType.WEIGHT

            val breeds = getRandomBreedsWithWeight.invoke(2)
            if (breeds.size < 2) {
                // Not enough data, end the game
                _events.tryEmit(Event.NavigateToResult(_state.value.score))
                return@launch
            }

            _state.update {
                it.copy(
                    breedLeft = breeds[0],
                    breedRight = breeds[1],
                    comparisonType = type,
                    isLoading = false
                )
            }
        }
    }

    fun onBreedSelected(isLeftSelected: Boolean) {
        val currentState = _state.value
        val left = currentState.breedLeft ?: return
        val right = currentState.breedRight ?: return

        val leftValue = when (currentState.comparisonType) {
            ComparisonType.WEIGHT -> left.maxWeightKg
            ComparisonType.HEIGHT -> left.maxHeightCm
        }
        val rightValue = when (currentState.comparisonType) {
            ComparisonType.WEIGHT -> right.maxWeightKg
            ComparisonType.HEIGHT -> right.maxHeightCm
        }

        // Within 10% tolerance, player always wins
        val tolerance = maxOf(leftValue, rightValue) * 0.10
        val isEqual = kotlin.math.abs(leftValue - rightValue) <= tolerance

        val isCorrect = isEqual ||
            (isLeftSelected && leftValue >= rightValue) ||
            (!isLeftSelected && rightValue >= leftValue)

        Analytics.analyticsGameAnswer(isCorrect, currentState.stage, Analytics.MODE_BIGGER_SMALLER)

        if (isCorrect) {
            _state.update {
                it.copy(
                    score = it.score + 1,
                    stage = it.stage + 1,
                    lastResult = AnswerResult.CORRECT
                )
            }
        } else {
            val newLives = currentState.lives - 1
            _state.update {
                it.copy(
                    lives = newLives,
                    stage = it.stage + 1,
                    lastResult = AnswerResult.INCORRECT
                )
            }
        }

        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("game_mode", Analytics.MODE_BIGGER_SMALLER)
            setCustomKey("current_stage", _state.value.stage)
            setCustomKey("current_score", _state.value.score)
            setCustomKey("lives_remaining", _state.value.lives)
        }
    }

    fun proceedAfterResult() {
        val currentState = _state.value
        if (currentState.lives < 1) {
            Analytics.analyticsGameFinished(currentState.score.toString(), Analytics.MODE_BIGGER_SMALLER)
            _events.tryEmit(Event.NavigateToResult(currentState.score))
        } else {
            if (currentState.stage % 6 == 0) {
                _events.tryEmit(Event.ShowRewardedAd(true))
            }
            loadNewRound()
        }
    }
}
