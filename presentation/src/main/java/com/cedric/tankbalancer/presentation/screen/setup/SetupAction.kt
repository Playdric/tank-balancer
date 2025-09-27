package com.cedric.tankbalancer.presentation.screen.setup

import com.cedric.domain.model.FuelUnit

sealed interface SetupAction {
    data object ConfirmSetup: SetupAction
    data class ChangedLeftFuel(val newFuelQuantity: String): SetupAction
    data class ChangedRightFuel(val newFuelQuantity: String): SetupAction
    data class ChangedFuelFlow(val newFuelFlow: String): SetupAction
    data class ChangedFuelUnit(val newFuelUnit: FuelUnit): SetupAction
    data class ChangedStartingTank(val newStartingTank: Int): SetupAction

    data object AcknowledgeError: SetupAction
}
