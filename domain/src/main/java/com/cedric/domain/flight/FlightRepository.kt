package com.cedric.domain.flight

interface FlightRepository {
    suspend fun takeOff(
        initialLeftFuel: Double,
        initialRightFuel: Double,
        initialFuelFlow: Double,
    )

    suspend fun lapTime(error: (LapTimeError?) -> Unit)

    enum class LapTimeError {
        NO_CURRENT_FLIGHT,
        NO_CURRENT_LAP_TIME
    }
}
