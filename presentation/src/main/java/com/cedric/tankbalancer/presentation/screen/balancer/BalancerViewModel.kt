package com.cedric.tankbalancer.presentation.screen.balancer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedric.tankbalancer.domain.flight.FlightRepository
import com.cedric.tankbalancer.domain.flight.FlightTicker
import com.cedric.tankbalancer.domain.formatter.TimeFormatter
import com.cedric.tankbalancer.domain.formatter.toStringWithMaxChar
import com.cedric.tankbalancer.domain.model.AircraftTank
import com.cedric.tankbalancer.domain.model.Flight
import com.cedric.tankbalancer.domain.model.RemainingFuel
import com.cedric.tankbalancer.domain.model.currentTank
import com.cedric.tankbalancer.domain.model.endTimestamp
import com.cedric.tankbalancer.domain.usecase.RemainingFuelUseCase
import com.cedric.tankbalancer.domain.usecase.TotalTankTimeUseCase
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class BalancerViewModel(
    val flightRepository: FlightRepository,
    val tickerRepository: FlightTicker,
    val remainingFuelUseCase: RemainingFuelUseCase,
    val totalTankTimeUseCase: TotalTankTimeUseCase,
    val arguments: TankBalancerNavEntry.BalancerScreen.Arguments?,
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<TankBalancerNavEntry>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    /**
     * Flow used to display to the user the quick changing fuel flow
     */
    private val currentFuelFLow: MutableStateFlow<Double?> = MutableStateFlow(null)

    init {
        if (arguments == null) {
            Timber.tag(TAG).i("Arguments are null, restoring previous flight")
            tickerRepository.startTick()
        }
    }

    val uiState = combine(
        flightRepository.currentFlight,
        tickerRepository.ticker,
        currentFuelFLow
    ) { currentFlight, _, currentFuelFlow ->
        if (currentFlight == null) return@combine BalancerUiState(flightStatus = FlightStatus.BEFORE_TAKE_OFF)

        val remainingFuel = remainingFuelUseCase(currentFlight)

        BalancerUiState(
            currentTank = currentFlight.currentTank,
            fuelFlow = currentFuelFlow ?: currentFlight.fuelFlows.lastOrNull()?.fuelFlow ?: 0.0,
            totalTime = TimeFormatter.formatElapsedTimeSince(currentFlight.takeOffTimestamp),
            currentTankTime = currentFlight.getCurrentTankLapTime(),
            leftTankTotalTime = TimeFormatter.formatTimestamp(totalTankTimeUseCase(currentFlight, AircraftTank.LEFT)),
            leftTankFuel = remainingFuel.left.toStringWithMaxChar(maxTotalDigits = 3, maxDecimalPlaces = 1),
            leftTankFuelPercent = remainingFuel.left / currentFlight.initialLeftFuel,
            rightTankTotalTime = TimeFormatter.formatTimestamp(totalTankTimeUseCase(currentFlight, AircraftTank.RIGHT)),
            rightTankFuel = remainingFuel.right.toStringWithMaxChar(maxTotalDigits = 3, maxDecimalPlaces = 1),
            rightTankFuelPercent = remainingFuel.right / currentFlight.initialRightFuel,
            lapTimes = currentFlight.getUiLapTimes(),
            flightStatus = if (currentFlight.endTimestamp == null) FlightStatus.FLYING else FlightStatus.LANDED,
            range = getRange(remainingFuel = remainingFuel, currentFuelFlow = currentFuelFlow, currentFlight = currentFlight)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        BalancerUiState()
    )

    fun onAction(action: BalancerAction) {
        when (action) {
            is BalancerAction.TakeOff -> takeOff()
            BalancerAction.IncreaseFuelFlow -> onChangeFuelFlow(increase = true)
            BalancerAction.DecreaseFuelFlow -> onChangeFuelFlow(increase = false)
            BalancerAction.ValidFuelFlow -> onValidateFuelFlow()
            BalancerAction.SwitchTank -> onSwitchTank()
            BalancerAction.ConfirmLanding -> onConfirmLanding()
        }
    }

    private fun getRange(remainingFuel: RemainingFuel, currentFlight: Flight, currentFuelFlow: Double?): String {
        val totalFuel = remainingFuel.left + remainingFuel.right
        val fuelFlow = currentFuelFlow ?: currentFlight.fuelFlows.lastOrNull()?.fuelFlow ?: 0.0

        return if (fuelFlow <= 0) {
            "--:--"
        } else {
            val rangeInHours = totalFuel / fuelFlow

            TimeFormatter.formatHours(rangeInHours)
        }
    }

    private fun onConfirmLanding() {
        viewModelScope.launch {
            flightRepository.land(onError = {}) {
                tickerRepository.stopTick()
            }
        }
    }

    private fun onSwitchTank() {
        viewModelScope.launch {
            flightRepository.lapTime(error = { error ->
                //TODO
            }) { newTank ->
            }
        }
    }

    private fun onValidateFuelFlow() {
        viewModelScope.launch {
            flightRepository.newFuelFlow(newFuelFlow = uiState.value.fuelFlow) { error ->
                //TODO
            }
            currentFuelFLow.emit(null)
        }
    }

    private fun onChangeFuelFlow(increase: Boolean) {
        val currentFuelFlow = uiState.value.fuelFlow
        val newFuelFlow = if (increase) {
            currentFuelFlow + FUEL_FLOW_CHANGE_RATE
        } else {
            currentFuelFlow - FUEL_FLOW_CHANGE_RATE
        }.coerceAtLeast(0.0)
        this.currentFuelFLow.value = newFuelFlow
    }


    private fun takeOff() {
        viewModelScope.launch {
            arguments?.let { args ->
                flightRepository.takeOff(
                    initialLeftFuel = args.initialFuelLeft,
                    initialRightFuel = args.initialFuelRight,
                    initialFuelFlow = args.initialFuelFlow,
                    initialTank = args.initialTank,
                )
                tickerRepository.startTick()
            }
        }
    }

    companion object {
        private const val TAG = "BalancerViewModel"
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
