package com.cedric.tankbalancer.presentation.screen.setup

import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.FuelUnit

data class SetupUiState(
    val leftFuel: Double = 10.0,
    val rightFuel: Double = 10.0,
    val fuelFlow: Double = 10.0,
    val fuelUnit: FuelUnit = FuelUnit.METRIC,
    val startingTank: AircraftTank = AircraftTank.LEFT,

    val error : SetupError? = null,
)

sealed interface SetupError {
    data object FuelError: SetupError
}
