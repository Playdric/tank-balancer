package com.cedric.tankbalancer.presentation.screen.balancer

import com.cedric.domain.model.AircraftTank


data class BalancerUiState(
    val currentTank: AircraftTank? = null,

    val fuelFlow: Double = 10.0,

    val totalTime: String = "00:00",

    val leftTankLapTime: String = "00:00",
    val leftTankTotalTime: String = "00:00",
    val leftTankFuel: Double = 0.0,

    val rightTankLapTime: String = "00:00",
    val rightTankTotalTime: String = "00:00",
    val rightTankFuel: Double = 0.0,

    val range: String = "00:00",

    val isFlying: Boolean = false,

    val balancerError: BalancerError? = null,
)

enum class BalancerError {
    TAKE_OFF_WILE_NOT_SETUP,
}
