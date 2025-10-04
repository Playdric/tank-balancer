package com.cedric.tankbalancer.presentation.screen.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedric.domain.flight.FlightRepository
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class LauncherViewModel(
    val flightRepository: FlightRepository,
) : ViewModel() {

    private val showRestorePreviousStatePopup = flightRepository.currentFlight.map { currentFlight ->
        currentFlight?.lapTimes?.firstOrNull { it.endTimestamp == null } != null
    }.onEach {
        if (!it) {
            _navigationEvent.emit(TankBalancerNavEntry.SetupScreen)
        }
    }

    private val isLoading = MutableStateFlow(true)

    val uiState: StateFlow<LauncherUiState> =
        combine(
            isLoading,
            showRestorePreviousStatePopup
        ) { isLoading, showPopup ->
            LauncherUiState(
                isLoading = isLoading,
                showRestorePreviousStatePopup = showPopup
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = LauncherUiState()
        )


    private val _navigationEvent = MutableSharedFlow<TankBalancerNavEntry?>()
    val navigationEvent = _navigationEvent.asSharedFlow().mapNotNull { it }

    fun onAction(action: LauncherAction) {
        when (action) {
            is LauncherAction.RestorePopupAction -> onRestorePopupAction(restorePreviousState = action.restorePreviousState)
        }
    }

    private fun onRestorePopupAction(restorePreviousState: Boolean) {
        viewModelScope.launch {
            isLoading.emit(false)
            if (restorePreviousState) {
                _navigationEvent.emit(TankBalancerNavEntry.BalancerScreen())
            } else {
                flightRepository.land(onError = {
                    Timber.tag(TAG).e("Finish flight error")
                }, onSuccess = {})
                _navigationEvent.tryEmit(TankBalancerNavEntry.SetupScreen)
                //TODO finish last flight with a flag or smth
            }
        }
    }

    companion object {
        private const val TAG = "LauncherViewModel"
    }

}
