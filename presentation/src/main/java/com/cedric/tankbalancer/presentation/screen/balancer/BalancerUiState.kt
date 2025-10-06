package com.cedric.tankbalancer.presentation.screen.balancer

import android.os.Parcelable
import androidx.annotation.FloatRange
import com.cedric.tankbalancer.domain.model.AircraftTank
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize

@Parcelize
data class BalancerUiState(
    val currentTank: AircraftTank? = null,
    val fuelFlow: Double = 10.0,
    val totalTime: String = "00:00",
    val currentTankTime: String = "00:00",

    val leftTankLapTime: String = "00:00",
    val leftTankTotalTime: String = "00:00",
    val leftTankFuel: String = "0.0",
    @param:FloatRange(0.0, 1.0) val leftTankFuelPercent: Double = 0.0,

    val rightTankLapTime: String = "00:00",
    val rightTankTotalTime: String = "00:00",
    val rightTankFuel: String = "0.0",
    @param:FloatRange(0.0, 1.0) val rightTankFuelPercent: Double = 0.0,

    val range: String = "00:00",

    val lapTimes: ImmutableList<UiLapTime> = persistentListOf(),

    val flightStatus: FlightStatus = FlightStatus.BEFORE_TAKE_OFF,

    val balancerError: BalancerError? = null,
) : Parcelable

@Parcelize
data class UiLapTime(
    val tank: AircraftTank,
    val startTime: String,
) : Parcelable

@Parcelize
enum class BalancerError : Parcelable

@Parcelize
enum class FlightStatus : Parcelable {
    BEFORE_TAKE_OFF,
    FLYING,
    STOPOVER,
    LANDED
}
