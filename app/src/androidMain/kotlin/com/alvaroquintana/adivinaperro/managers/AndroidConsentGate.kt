package com.alvaroquintana.adivinaperro.managers

import android.app.Activity

class AndroidConsentGate(
    private val activityProvider: () -> Activity,
    private val consent: ConsentManager
) : ConsentGate {
    override val isPrivacyOptionsRequired: Boolean
        get() = consent.isPrivacyOptionsRequired

    override fun showPrivacyOptionsForm() {
        consent.showPrivacyOptionsForm(activityProvider()) { _ -> }
    }
}
