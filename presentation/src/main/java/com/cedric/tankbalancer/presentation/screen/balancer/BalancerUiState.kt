package com.cedric.tankbalancer.presentation.screen.balancer

import com.cedric.domain.model.AircraftTank
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


data class BalancerUiState(
    val currentTank: AircraftTank? = null,
    val fuelFlow: Double = 10.0,
    val totalTime: String = "00:00",
    val currentTankTime: String = "00:00",

    val leftTankLapTime: String = "00:00",
    val leftTankTotalTime: String = "00:00",
    val leftTankFuel: Double = 0.0,

    val rightTankLapTime: String = "00:00",
    val rightTankTotalTime: String = "00:00",
    val rightTankFuel: Double = 0.0,

    val range: String = "00:00",

    val lapTimes: ImmutableList<UiLapTime> = persistentListOf(),

    val flightStatus: FlightStatus = FlightStatus.BEFORE_TAKE_OFF,

    val balancerError: BalancerError? = null,
)

data class UiLapTime(
    val tank: AircraftTank,
    val startTime: String,
)

enum class BalancerError

enum class FlightStatus {
    BEFORE_TAKE_OFF,
    FLYING,
    STOPOVER,
    LANDED
}
