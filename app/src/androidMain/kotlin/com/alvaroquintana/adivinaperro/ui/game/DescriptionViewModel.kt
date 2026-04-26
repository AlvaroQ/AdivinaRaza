package com.alvaroquintana.adivinaperro.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.usecases.GetRandomBreedsWithDescription
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DescriptionViewModel(
    private val getRandomBreedsWithDescription: GetRandomBreedsWithDescription
) : ViewModel() {

    data class GameState(
        val correctBreed: Dog? = null,
        val options: List<String> = emptyList(),
        val descriptionText: String = "",
        val score: Int = 0,
        val lives: Int = 3,
        val stage: Int = 1,
        val roundId: Int = 0,
        val isLoading: Boolean = true,
        val lastResult: AnswerResult? = null,
        val correctName: String = ""
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
            Analytics.analyticsScreenViewed(Analytics.SCREEN_DESCRIPTION_GAME)
            FirebaseCrashlytics.getInstance().setCustomKey("current_screen", Analytics.SCREEN_DESCRIPTION_GAME)
        }
        loadNewRound()
        _events.tryEmit(Event.ShowBannerAd(true))
    }

    fun loadNewRound() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, lastResult = null) }

            // Get 4 breeds with temperament data
            val breeds = getRandomBreedsWithDescription.invoke(4)
            if (breeds.size < 4) {
                _events.tryEmit(Event.NavigateToResult(_state.value.score))
                return@launch
            }

            // Pick one as the correct answer
            val correctBreed = breeds.random()

            // Build description from temperament + other fields
            val description = buildDescription(correctBreed)

            // Shuffle options
            val options = breeds.map { it.name }.shuffled()

            _state.update {
                it.copy(
                    correctBreed = correctBreed,
                    options = options,
                    descriptionText = description,
                    correctName = correctBreed.name,
                    roundId = it.roundId + 1,
                    isLoading = false
                )
            }
        }
    }

    private fun buildDescription(dog: Dog): String {
        val parts = mutableListOf<String>()

        if (dog.temperament.isNotBlank()) {
            parts.add("Esta raza se caracteriza por ser ${dog.temperament.lowercase()}.")
        }
        if (dog.sizeCategory.isNotBlank()) {
            parts.add("Es un perro de tamaño ${dog.sizeCategory.lowercase()}.")
        }
        if (dog.origin.isNotBlank()) {
            parts.add("Originario de ${dog.origin}.")
        }
        if (dog.breedGroup.isNotBlank()) {
            parts.add("Pertenece al grupo ${dog.breedGroup}.")
        }
        if (dog.coatType.isNotBlank()) {
            parts.add("Tiene un pelaje ${dog.coatType.lowercase()}.")
        }
        if (dog.lifeSpanMin > 0 && dog.lifeSpanMax > 0) {
            parts.add("Esperanza de vida: ${dog.lifeSpanMin}-${dog.lifeSpanMax} años.")
        }

        return if (parts.isEmpty()) {
            "Una raza de perro increíble. ¿Puedes adivinar cuál es?"
        } else {
            parts.joinToString(" ")
        }
    }

    fun onOptionSelected(selectedName: String) {
        val currentState = _state.value
        val isCorrect = selectedName == currentState.correctName

        runCatching {
            Analytics.analyticsGameAnswer(isCorrect, currentState.stage, Analytics.MODE_DESCRIPTION)
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
            val newLives = currentState.lives - 1
            _state.update {
                it.copy(
                    lives = newLives,
                    stage = it.stage + 1,
                    lastResult = AnswerResult.INCORRECT
                )
            }
        }

        runCatching {
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("game_mode", Analytics.MODE_DESCRIPTION)
                setCustomKey("current_stage", _state.value.stage)
                setCustomKey("current_score", _state.value.score)
                setCustomKey("lives_remaining", _state.value.lives)
            }
        }
    }

    fun proceedAfterResult() {
        val currentState = _state.value
        if (currentState.lives < 1) {
            runCatching {
                Analytics.analyticsGameFinished(currentState.score.toString(), Analytics.MODE_DESCRIPTION)
            }
            _events.tryEmit(Event.NavigateToResult(currentState.score))
        } else {
            if (currentState.stage % 6 == 0) {
                _events.tryEmit(Event.ShowRewardedAd(true))
            }
            loadNewRound()
        }
    }
}
