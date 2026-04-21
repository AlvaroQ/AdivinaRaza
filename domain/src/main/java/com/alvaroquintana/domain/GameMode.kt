package com.alvaroquintana.domain

sealed class GameMode(val id: String, val displayName: String) {
    object Classic : GameMode("classic", "Adivina la Raza")
    object BiggerOrSmaller : GameMode("bigger_smaller", "Mayor o Menor")
    object GuessByDescription : GameMode("description", "Adivina por Descripción")
    object FciTrivia : GameMode("fci_group", "FCI Trivia")
}
