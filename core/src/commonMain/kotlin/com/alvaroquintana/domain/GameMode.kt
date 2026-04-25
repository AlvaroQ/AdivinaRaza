package com.alvaroquintana.domain

sealed class GameMode(val id: String, val displayName: String) {
    data object Classic : GameMode("classic", "Adivina la Raza")
    data object BiggerOrSmaller : GameMode("bigger_smaller", "Mayor o Menor")
    data object GuessByDescription : GameMode("description", "Adivina por Descripción")
    data object FciTrivia : GameMode("fci_group", "FCI Trivia")
}
