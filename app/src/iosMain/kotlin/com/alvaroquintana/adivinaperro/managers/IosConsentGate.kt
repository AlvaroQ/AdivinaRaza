package com.alvaroquintana.adivinaperro.managers

class IosConsentGate : ConsentGate {
    // ATT and EU consent flows arrive in phase 6c via Google UMP iOS SDK.
    override val isPrivacyOptionsRequired: Boolean = false
    override fun showPrivacyOptionsForm() = Unit
}
