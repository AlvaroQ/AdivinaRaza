package com.alvaroquintana.adivinaperro.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetRandomBreedsWithFciGroup
import com.alvaroquintana.adivinaperro.managers.ErrorTracker
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FciTriviaViewModel(
    private val getRandomBreedsWithFciGroup: GetRandomBreedsWithFciGroup
) : ViewModel() {

    data class GameState(
        val correctBreed: Dog? = null,
        val options: List<String> = emptyList(),
        val questionImage: String = "",
        val score: Int = 0,
        val lives: Int = 3,
        val stage: Int = 1,
        val roundId: Int = 0,
        val isLoading: Boolean = true,
        val lastResult: AnswerResult? = null,
        val correctAnswer: String = ""
    )

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
        runCatching {
            Analytics.analyticsScreenViewed(Analytics.SCREEN_FCI_TRIVIA)
            ErrorTracker.setCustomKey("current_screen", Analytics.SCREEN_FCI_TRIVIA)
        }
        loadNewRound()
        _events.tryEmit(Event.ShowBannerAd(true))
    }

    fun loadNewRound() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, lastResult = null) }

            // Pull a pool to maximize distinct group options.
            val pool = getRandomBreedsWithFciGroup.invoke(60)
                .filter { it.fciGroup > 0 }

            if (pool.isEmpty()) {
                _events.tryEmit(Event.NavigateToResult(_state.value.score))
                return@launch
            }

            val correct = pool.random()
            val correctGroup = correct.fciGroup
            val distractorGroups = pool.map { it.fciGroup }
                .distinct()
                .filter { it > 0 && it != correctGroup }
                .shuffled()
                .take(3)

            if (distractorGroups.size < 3) {
                _events.tryEmit(Event.NavigateToResult(_state.value.score))
                return@launch
            }

            val allGroups = (distractorGroups + correctGroup).shuffled()
            val options = allGroups.map { "Grupo $it" }

            _state.update {
                it.copy(
                    correctBreed = correct,
                    questionImage = correct.icon,
                    options = options,
                    correctAnswer = "Grupo $correctGroup",
                    roundId = it.roundId + 1,
                    isLoading = false
                )
            }
        }
    }

    fun onOptionSelected(selected: String) {
        val current = _state.value
        val isCorrect = selected == current.correctAnswer

        runCatching {
            Analytics.analyticsGameAnswer(isCorrect, current.stage, Analytics.MODE_FCI_TRIVIA)
        }

        if (isCorrect) {
            _state.update {
                it.copy(
                    score = it.score + 1,
                    stage = it.stage + 1,
                    lastResult = AnswerResult.CORRECT
                )
            }
        } else {
            val newLives = current.lives - 1
            _state.update {
                it.copy(
                    lives = newLives,
                    stage = it.stage + 1,
                    lastResult = AnswerResult.INCORRECT
                )
            }
        }

        runCatching {
            ErrorTracker.setCustomKey("game_mode", Analytics.MODE_FCI_TRIVIA)
            ErrorTracker.setCustomKey("current_stage", _state.value.stage)
            ErrorTracker.setCustomKey("current_score", _state.value.score)
            ErrorTracker.setCustomKey("lives_remaining", _state.value.lives)
        }
    }

    fun proceedAfterResult() {
        val current = _state.value
        if (current.lives < 1) {
            runCatching {
                Analytics.analyticsGameFinished(current.score.toString(), Analytics.MODE_FCI_TRIVIA)
            }
            _events.tryEmit(Event.NavigateToResult(current.score))
        } else {
            if (current.stage % 6 == 0) {
                _events.tryEmit(Event.ShowRewardedAd(true))
            }
            loadNewRound()
        }
    }
}
