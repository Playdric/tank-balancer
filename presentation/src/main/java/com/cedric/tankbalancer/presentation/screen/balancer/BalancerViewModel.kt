package com.cedric.tankbalancer.presentation.screen.balancer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedric.domain.flight.FlightRepository
import com.cedric.domain.flight.FlightTicker
import com.cedric.domain.formatter.TimeFormatter
import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.Flight
import com.cedric.domain.usecase.RemainingFuelUseCase
import com.cedric.domain.usecase.TotalTankTimeUseCase
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BalancerViewModel(
    val flightRepository: FlightRepository,
    val tickerRepository: FlightTicker,
    val remainingFuelUseCase: RemainingFuelUseCase,
    val totalTankTimeUseCase: TotalTankTimeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BalancerUiState())
    val uiState: StateFlow<BalancerUiState> =
        combine(
            _uiState,
            tickerRepository.ticker,
            flightRepository.currentFlight
        ) { state, _, currentFlight ->
            val remainingFuel = remainingFuelUseCase(currentFlight)
            val totalLeftTankTime = totalTankTimeUseCase(currentFlight = currentFlight, tank = AircraftTank.LEFT)
            val totalRightTankTime = totalTankTimeUseCase(currentFlight = currentFlight, tank = AircraftTank.RIGHT)
            state.copy(
                totalTime = TimeFormatter.formatElapsedTimeSince(currentFlight.takeOffTimestamp),
                leftTankTotalTime = TimeFormatter.formatTimestamp(totalLeftTankTime),
                rightTankTotalTime = TimeFormatter.formatTimestamp(totalRightTankTime),
                leftTankFuel = remainingFuel.left,
                rightTankFuel = remainingFuel.right,
                currentTankTime = currentFlight.getCurrentTankLapTime(),
                lapTimes = currentFlight.getUiLapTimes()

            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = BalancerUiState()
        )

    private val _navigationEvent = MutableSharedFlow<TankBalancerNavEntry>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private lateinit var arguments: TankBalancerNavEntry.BalancerScreen.Arguments

    fun setArguments(arguments: TankBalancerNavEntry.BalancerScreen.Arguments?) {
        arguments?.let { arg -> this@BalancerViewModel.arguments = arg }
    }

    fun onAction(action: BalancerAction) {
        when (action) {
            BalancerAction.AcknowledgeError -> acknowledgeError()
            BalancerAction.TakeOff -> takeOff()
            BalancerAction.IncreaseFuelFlow -> onChangeFuelFlow(increase = true)
            BalancerAction.DecreaseFuelFlow -> onChangeFuelFlow(increase = false)
            BalancerAction.ValidFuelFlow -> onValidateFuelFlow()
            BalancerAction.SwitchTank -> onSwitchTank()
            BalancerAction.ConfirmLanding -> onConfirmLanding()
        }
    }

    private fun onConfirmLanding() {
        viewModelScope.launch {
            flightRepository.land(onError = {}) {
                _uiState.update {
                    it.copy(
                        flightStatus = FlightStatus.LANDED
                    )
                }
                tickerRepository.stopTick()
            }
        }
    }

    private fun onSwitchTank() {
        viewModelScope.launch {
            flightRepository.lapTime(error = { error ->
                //TODO
            }) { newTank ->
                _uiState.update {
                    it.copy(
                        currentTank = newTank
                    )
                }
            }
        }
    }

    private fun onValidateFuelFlow() {
        viewModelScope.launch {
            flightRepository.newFuelFlow(newFuelFlow = _uiState.value.fuelFlow) { error ->
                //TODO
            }
        }
    }

    private fun onChangeFuelFlow(increase: Boolean) {
        _uiState.update { state ->
            val currentFuelFlow = state.fuelFlow
            val newFuelFlow = if (increase) {
                currentFuelFlow + FUEL_FLOW_CHANGE_RATE
            } else {
                currentFuelFlow - FUEL_FLOW_CHANGE_RATE
            }.coerceAtLeast(0.0)
            state.copy(fuelFlow = newFuelFlow)
        }

    }

    private fun takeOff() {
        viewModelScope.launch {
            flightRepository.takeOff(
                initialTank = arguments.initialTank,
                initialLeftFuel = arguments.initialFuelLeft,
                initialRightFuel = arguments.initialFuelRight,
                initialFuelFlow = arguments.initialFuelFlow,
            )
            _uiState.update {
                it.copy(
                    currentTank = arguments.initialTank,
                    leftTankFuel = arguments.initialFuelLeft,
                    rightTankFuel = arguments.initialFuelRight,
                    fuelFlow = arguments.initialFuelFlow,
                    flightStatus = FlightStatus.FLYING,
                )
            }
            tickerRepository.startTick()
        }
    }

    override fun onCleared() {
        tickerRepository.stopTick()
    }

    private fun acknowledgeError() {
        _uiState.update { it.copy(balancerError = null) }
    }

    companion object {
        private const val FUEL_FLOW_CHANGE_RATE = 0.5
    }
}

private fun Flight.getCurrentTankLapTime(): String =
    TimeFormatter.formatElapsedTimeSince(lapTimes.firstOrNull { it.endTimestamp == null }?.startTimestamp ?: 0L)

private fun Flight.getUiLapTimes(): PersistentList<UiLapTime> {
    return lapTimes
        .filter { lapTime -> lapTime.endTimestamp != null }
        .map { lapTime ->
            UiLapTime(
                tank = lapTime.tank,
                startTime = TimeFormatter.formatElapsedTimeSince(lapTime.startTimestamp, lapTime.endTimestamp ?: System.currentTimeMillis())
            )
        }.reversed()
        .toPersistentList()
}
