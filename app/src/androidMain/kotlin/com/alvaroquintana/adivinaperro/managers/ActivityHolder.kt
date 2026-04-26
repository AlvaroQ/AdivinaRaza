package com.alvaroquintana.adivinaperro.managers

import android.app.Activity

/**
 * Holds the currently visible Activity so platform-specific managers
 * (IntentLauncher, ConsentGate) can launch activities without taking a
 * Context per call. MainActivity registers itself in onResume / clears
 * in onPause.
 */
object ActivityHolder {
    @Volatile
    var current: Activity? = null
}
