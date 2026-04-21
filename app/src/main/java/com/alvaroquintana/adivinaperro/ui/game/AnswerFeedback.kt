package com.alvaroquintana.adivinaperro.ui.game

import com.alvaroquintana.adivinaperro.ui.components.AnswerState

internal fun applyAnswerFeedbackStates(
    answerStates: MutableList<AnswerState>,
    options: List<String>,
    correctAnswer: String,
    selectedIndex: Int
) {
    val correctIndex = options.indexOfFirst { it == correctAnswer }
    if (correctIndex >= 0) {
        answerStates[correctIndex] = AnswerState.CORRECT
    }

    if (selectedIndex != correctIndex && selectedIndex in answerStates.indices) {
        answerStates[selectedIndex] = AnswerState.WRONG
    }
}
