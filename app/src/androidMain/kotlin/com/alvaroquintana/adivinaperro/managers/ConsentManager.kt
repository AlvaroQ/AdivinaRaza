package com.alvaroquintana.adivinaperro.managers

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

class ConsentManager private constructor(context: Context) {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    fun gatherConsent(
        activity: Activity,
        onComplete: (FormError?) -> Unit
    ) {
        val params = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    onComplete(formError)
                }
            },
            { error ->
                onComplete(error)
            }
        )
    }

    fun showPrivacyOptionsForm(
        activity: Activity,
        onDismissed: (FormError?) -> Unit
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onDismissed)
    }

    companion object {
        @Volatile
        private var instance: ConsentManager? = null

        fun getInstance(context: Context): ConsentManager =
            instance ?: synchronized(this) {
                instance ?: ConsentManager(context).also { instance = it }
            }
    }
}
