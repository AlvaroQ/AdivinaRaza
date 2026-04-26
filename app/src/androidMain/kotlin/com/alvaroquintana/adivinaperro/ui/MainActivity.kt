package com.alvaroquintana.adivinaperro.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.alvaroquintana.adivinaperro.App
import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.managers.ActivityHolder
import com.alvaroquintana.adivinaperro.managers.AdMobConfig
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.adivinaperro.managers.ConsentManager
import com.alvaroquintana.adivinaperro.ui.theme.ThemeMode
import com.alvaroquintana.adivinaperro.utils.log
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.atomic.AtomicBoolean

private const val PREFS_THEME_MODE = "theme_mode"

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var consentManager: ConsentManager
    private val isMobileAdsInitialized = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)

        auth = Firebase.auth
        Analytics.initialize(this)

        consentManager = ConsentManager.getInstance(this)
        consentManager.gatherConsent(this) { error ->
            if (error != null) log(tag, "Consent error: ${error.errorCode} - ${error.message}")
            if (consentManager.canRequestAds) initializeMobileAdsSdk()
        }
        if (consentManager.canRequestAds) initializeMobileAdsSdk()

        val prefs = getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        val initialThemeMode = parseThemeMode(prefs.getString(PREFS_THEME_MODE, ThemeMode.SYSTEM.name))
        val adMobConfig = AdMobConfig(
            bannerGame = getString(R.string.BANNER_GAME),
            bannerInfo = getString(R.string.BANNER_INFO),
            bonificadoGame = getString(R.string.BONIFICADO_GAME),
            interstitialGameOver = getString(R.string.INTERSTICIAL_GAME_OVER)
        )

        setContent {
            App(
                initialThemeMode = initialThemeMode,
                adMobConfig = adMobConfig,
                onThemeModePersist = { mode ->
                    prefs.edit { putString(PREFS_THEME_MODE, mode.name) }
                }
            )
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onResume() {
        super.onResume()
        ActivityHolder.current = this
    }

    override fun onPause() {
        if (ActivityHolder.current === this) ActivityHolder.current = null
        super.onPause()
    }

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    log(tag, "signInAnonymously:success")
                    updateUI(auth.currentUser)
                } else {
                    log(tag, "signInAnonymously:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val isSignedIn = user != null
        log(tag, "updateUI, isSignedON = $isSignedIn")
        if (!isSignedIn) {
            signInAnonymously()
        } else {
            FirebaseCrashlytics.getInstance().setUserId(user.uid)
            log(tag, "updateUI, you are login in")
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitialized.getAndSet(true)) return
        MobileAds.initialize(this)
    }
}

private fun parseThemeMode(value: String?): ThemeMode = try {
    if (value == null) ThemeMode.SYSTEM else ThemeMode.valueOf(value)
} catch (_: Exception) {
    ThemeMode.SYSTEM
}
