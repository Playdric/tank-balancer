package com.cedric.tankbalancer.presentation.screen.setup

import android.os.Parcelable
import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.FuelUnit
import kotlinx.parcelize.Parcelize

@Parcelize
data class SetupUiState(
    val leftFuel: Double = 10.0,
    val rightFuel: Double = 10.0,
    val fuelFlow: Double = 10.0,
    val fuelUnit: FuelUnit = FuelUnit.METRIC,
    val startingTank: AircraftTank = AircraftTank.LEFT,

    val error : SetupError? = null,
) : Parcelable


sealed interface SetupError : Parcelable {
    @Parcelize
    data object FuelError : SetupError
}
