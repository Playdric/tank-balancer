package com.cedric.tankbalancer.presentation.screen.balancer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedric.domain.flight.FlightRepository
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BalancerViewModel(
    val flightRepository: FlightRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BalancerUiState())
    val uiState: StateFlow<BalancerUiState> = _uiState.asStateFlow()

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
        }
    }

    private fun takeOff() {
        viewModelScope.launch {
            flightRepository.takeOff(
                initialLeftFuel = arguments.initialFuelLeft,
                initialRightFuel = arguments.initialFuelRight,
                initialFuelFlow = arguments.initialFuelFlow,
            )
            _uiState.update { it.copy(
                currentTank = arguments.initialTank,
                leftTankFuel = arguments.initialFuelLeft,
                rightTankFuel = arguments.initialFuelRight,
                fuelFlow = arguments.initialFuelFlow,
                isFlying = true,
            ) }
        }
    }

    private fun acknowledgeError() {
        _uiState.update { it.copy(balancerError = null) }
    }


}
