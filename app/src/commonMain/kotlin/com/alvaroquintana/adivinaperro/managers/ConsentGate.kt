package com.alvaroquintana.adivinaperro.managers

interface ConsentGate {
    val isPrivacyOptionsRequired: Boolean
    fun showPrivacyOptionsForm()
}
