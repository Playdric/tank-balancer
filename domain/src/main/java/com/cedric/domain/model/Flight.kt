package com.cedric.domain.model

data class Flight(
    val takeOffTimestamp: Long,
    val initialLeftFuel: Double,
    val initialRightFuel: Double,
    val lapTimes: List<LapTime>,
    val fuelFlows: List<FuelFlow>
)

val Flight.endTimestamp: Long?
    get() {
        val sorted = lapTimes.sortedBy { time -> time.endTimestamp }
        return if (sorted.first().endTimestamp == null) {
            null
        } else {
            sorted.last().endTimestamp
        }
    }

val Flight.currentTank: AircraftTank?
    get() {
        return lapTimes.firstOrNull { it.endTimestamp == null }?.tank
    }
