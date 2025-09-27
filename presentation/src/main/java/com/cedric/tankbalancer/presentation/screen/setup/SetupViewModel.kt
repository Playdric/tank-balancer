package com.cedric.tankbalancer.presentation.screen.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.FuelUnit
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<TankBalancerNavEntry>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onAction(action: SetupAction) {
        when (action) {
            SetupAction.AcknowledgeError -> onAcknowledgeError()
            is SetupAction.ConfirmSetup -> onConfirmSetup()
            is SetupAction.ChangedFuelUnit -> onChangedFuelUnit(action.newFuelUnit)
            is SetupAction.ChangedLeftFuel -> onChangeLeftFuelValue(action.newFuelQuantity)
            is SetupAction.ChangedRightFuel -> onChangeRightFuelValue(action.newFuelQuantity)
            is SetupAction.ChangedFuelFlow -> onChangeFuelFlow(action.newFuelFlow)
            is SetupAction.ChangedStartingTank -> onChangeStartingTank(action.newStartingTank)
        }
    }

    private fun onChangeStartingTank(newStartingTank: Int) {
        _uiState.update { it.copy(startingTank = AircraftTank.fromId(newStartingTank)) }
    }

    private fun onChangeFuelFlow(newFuelFlow: String) {
        validateFuel(newFuelFlow)?.let { fuelValue ->
            _uiState.update { it.copy(fuelFlow = fuelValue) }
        } ?: run {
            _uiState.update { it.copy(error = SetupError.FuelError) }
        }
    }

    private fun onChangedFuelUnit(newFuelUnit: FuelUnit) {
        _uiState.update { it.copy(fuelUnit = newFuelUnit) }
    }

    private fun onChangeLeftFuelValue(newFuel: String) {
        validateFuel(newFuel)?.let { fuelValue ->
            _uiState.update { it.copy(leftFuel = fuelValue) }
        } ?: run {
            _uiState.update { it.copy(error = SetupError.FuelError) }
        }
    }

    private fun onChangeRightFuelValue(newFuel: String) {
        validateFuel(newFuel)?.let { fuelValue ->
            _uiState.update { it.copy(rightFuel = fuelValue) }
        } ?: run {
            _uiState.update { it.copy(error = SetupError.FuelError) }
        }
    }

    private fun validateFuel(fuel: String): Double? {
        return fuel.asDoubleOrNull()
    }

    private fun onConfirmSetup() {
        viewModelScope.launch {
            _navigationEvent.emit(TankBalancerNavEntry.BalancerScreen(
                arguments = TankBalancerNavEntry.BalancerScreen.Arguments(
                    initialFuelLeft = _uiState.value.leftFuel,
                    initialFuelRight = _uiState.value.rightFuel,
                    initialFuelFlow = _uiState.value.fuelFlow,
                    initialTank = _uiState.value.startingTank,
                )
            ))
        }
    }

    private fun onAcknowledgeError() {

    }
}

fun String.asDoubleOrNull(): Double? {
    val normalized = this.replace(',', '.')
    return normalized.toDoubleOrNull()

}
