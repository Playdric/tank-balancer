package com.cedric.domain.model

data class Flight(
    val takeOffTimestamp: Long,
    val initialLeftFuel: Double,
    val initialRightFuel: Double,
    val lapTimes: List<LapTime>,
    val fuelFlows: List<FuelFlow>
)
