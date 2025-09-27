package com.cedric.data.flight

import com.cedric.data.db.flight.Flight
import com.cedric.data.db.flight.FlightDao
import com.cedric.data.db.laptime.LapTime
import com.cedric.data.db.laptime.LapTimeDao
import com.cedric.domain.flight.FlightRepository
import com.cedric.domain.model.AircraftTank
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class FlightRepositoryImpl(
    val flightDao: FlightDao,
    val lapTimeDao: LapTimeDao,
    val dispatcher: CoroutineContext = Dispatchers.Default,
) : FlightRepository {

    override suspend fun takeOff(initialLeftFuel: Double, initialRightFuel: Double, initialFuelFlow: Double) {
        withContext(dispatcher) {
            val startTimestamp = System.currentTimeMillis()
            val insertedFlightId = flightDao.insertFlight(
                Flight(
                    startTimestamp = startTimestamp,
                    endTimestamp = null,
                    initialLeftFuel = initialLeftFuel,
                    initialRightFuel = initialRightFuel,
                    initialFuelFlow = initialFuelFlow
                )
            )
            Timber.tag("COUCOU").d("FlightRepositoryImpl::takeOff() called, insertedFlightId=$insertedFlightId")
            val insertedLapTimeId = lapTimeDao.insertLapTime(
                LapTime(
                    flightId = insertedFlightId,
                    tank = AircraftTank.LEFT,
                    startTimestamp = startTimestamp,
                    endTimestamp = null
                )
            )
            Timber.tag("COUCOU").d("FlightRepositoryImpl::takeOff() called insertedLapTimeId=$insertedLapTimeId")
        }
    }

    override suspend fun lapTime(error: (FlightRepository.LapTimeError?) -> Unit) {
        withContext(dispatcher) {
            val currentTimeStamp = System.currentTimeMillis()
            val currentFlight = flightDao.getCurrentFlightSync()

            if (currentFlight == null) {
                error(FlightRepository.LapTimeError.NO_CURRENT_FLIGHT)
                return@withContext
            }
            val currentLapTime = lapTimeDao.getCurrentLapTimeSync()
            if (currentLapTime == null) {
                error(FlightRepository.LapTimeError.NO_CURRENT_LAP_TIME)
                return@withContext
            }

            lapTimeDao.updateLapTime(currentLapTime.copy(endTimestamp = currentTimeStamp))

            lapTimeDao.insertLapTime(
                LapTime(
                    flightId = currentLapTime.flightId,
                    tank = when (currentLapTime.tank) {
                        AircraftTank.LEFT -> AircraftTank.RIGHT
                        AircraftTank.RIGHT -> AircraftTank.LEFT
                    },
                    startTimestamp = currentTimeStamp,
                    endTimestamp = null

                )
            )


        }
    }
}
