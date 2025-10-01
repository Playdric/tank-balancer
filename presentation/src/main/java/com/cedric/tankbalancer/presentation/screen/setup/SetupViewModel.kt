package com.cedric.tankbalancer.presentation.screen.setup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.FuelUnit
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SetupViewModel(
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uiState = savedStateHandle.getStateFlow(KEY_SAVE_STATE_SETUP, SetupUiState())

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
        updateSetupUiState { it.copy(startingTank = AircraftTank.fromId(newStartingTank)) }
    }

    private fun onChangeFuelFlow(newFuelFlow: String) {
        validateFuel(newFuelFlow)?.let { fuelValue ->
            updateSetupUiState { it.copy(fuelFlow = fuelValue) }
        } ?: run {
            updateSetupUiState { it.copy(error = SetupError.FuelError) }
        }
    }

    private fun onChangedFuelUnit(newFuelUnit: FuelUnit) {
        updateSetupUiState { it.copy(fuelUnit = newFuelUnit) }
    }

    private fun onChangeLeftFuelValue(newFuel: String) {
        validateFuel(newFuel)?.let { fuelValue ->
            updateSetupUiState { it.copy(leftFuel = fuelValue) }
        } ?: run {
            updateSetupUiState { it.copy(error = SetupError.FuelError) }
        }
    }

    private fun onChangeRightFuelValue(newFuel: String) {
        validateFuel(newFuel)?.let { fuelValue ->
            updateSetupUiState { it.copy(rightFuel = fuelValue) }
        } ?: run {
            updateSetupUiState { it.copy(error = SetupError.FuelError) }
        }
    }

    private fun validateFuel(fuel: String): Double? {
        return fuel.asDoubleOrNull()
    }

    private fun onConfirmSetup() {
        viewModelScope.launch {
            _navigationEvent.emit(TankBalancerNavEntry.BalancerScreen(
                arguments = TankBalancerNavEntry.BalancerScreen.Arguments(
                    initialFuelLeft = uiState.value.leftFuel,
                    initialFuelRight = uiState.value.rightFuel,
                    initialFuelFlow = uiState.value.fuelFlow,
                    initialTank = uiState.value.startingTank,
                )
            ))
        }
    }

    private fun onAcknowledgeError() {

    }

    private fun updateSetupUiState(function: (SetupUiState) -> SetupUiState) {
        savedStateHandle[KEY_SAVE_STATE_SETUP] = function(uiState.value)
    }

    companion object {
        const val KEY_SAVE_STATE_SETUP = "SAVE_KEY_SETUP"
    }
}

fun String.asDoubleOrNull(): Double? {
    val normalized = this.replace(',', '.')
    return normalized.toDoubleOrNull()

}
