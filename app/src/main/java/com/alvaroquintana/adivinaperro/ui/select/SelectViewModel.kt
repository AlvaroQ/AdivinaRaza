package com.alvaroquintana.adivinaperro.ui.select

import androidx.lifecycle.ViewModel
import com.alvaroquintana.adivinaperro.managers.Analytics

class SelectViewModel : ViewModel() {

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_SELECT_GAME)
    }
}