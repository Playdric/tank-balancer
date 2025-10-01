package com.cedric.domain.flight

import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.Flight
import kotlinx.coroutines.flow.Flow

interface FlightRepository {

    val currentFlight: Flow<Flight?>

    suspend fun takeOff(
        initialTank: AircraftTank,
        initialLeftFuel: Double,
        initialRightFuel: Double,
        initialFuelFlow: Double,
    )

    suspend fun lapTime(error: (LapTimeError?) -> Unit = {}, success: (AircraftTank) -> Unit)
    enum class LapTimeError {
        NO_CURRENT_FLIGHT,
        NO_CURRENT_LAP_TIME,
        INSERTION_FAILED,
    }

    suspend fun newFuelFlow(newFuelFlow: Double, error: (FuelFlowError?) -> Unit)
    enum class FuelFlowError {
        NO_CURRENT_FLIGHT,
        NO_CURRENT_LAP_TIME
    }

    suspend fun land(onError: () -> Unit, onSuccess: () -> Unit)


}
