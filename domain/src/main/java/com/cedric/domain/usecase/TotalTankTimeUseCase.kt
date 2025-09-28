package com.cedric.domain.usecase

import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.Flight

class TotalTankTimeUseCase {
    operator fun invoke(currentFlight: Flight, tank: AircraftTank): Long {
        return currentFlight
            .lapTimes
            .filter { lapTime ->
                lapTime.tank == tank
            }.sumOf {
                (it.endTimestamp ?: System.currentTimeMillis()) - it.startTimestamp
            }
    }
}
