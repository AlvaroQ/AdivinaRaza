package com.alvaroquintana.adivinaperro.ui.result

import androidx.lifecycle.ViewModel
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.adivinaperro.managers.Settings
import com.alvaroquintana.adivinaperro.utils.Constants.RECORD_PERSONAL
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultViewModel(private val settings: Settings) : ViewModel() {

    private val _navigation = MutableSharedFlow<Navigation>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigation: SharedFlow<Navigation> = _navigation.asSharedFlow()

    private val _personalRecord = MutableStateFlow("")
    val personalRecord: StateFlow<String> = _personalRecord.asStateFlow()

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_RESULT)
    }

    fun getPersonalRecord(points: Int) {
        val stored = settings.getInt(RECORD_PERSONAL, 0)
        if (points > stored) {
            settings.putInt(RECORD_PERSONAL, points)
            _personalRecord.value = points.toString()
        } else {
            _personalRecord.value = stored.toString()
        }
    }

    fun navigateToSelect() {
        Analytics.analyticsClicked(Analytics.BTN_PLAY_AGAIN)
        _navigation.tryEmit(Navigation.Select)
    }

    fun navigateToRate() {
        Analytics.analyticsClicked(Analytics.BTN_RATE)
        _navigation.tryEmit(Navigation.Rate)
    }

    fun navigateToShare(points: Int) {
        Analytics.analyticsClicked(Analytics.BTN_SHARE)
        _navigation.tryEmit(Navigation.Share(points))
    }

    sealed class Navigation {
        data class Share(val points: Int) : Navigation()
        object Rate : Navigation()
        object Select : Navigation()
    }
}
