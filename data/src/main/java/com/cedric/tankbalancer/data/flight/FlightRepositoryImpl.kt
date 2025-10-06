package com.cedric.tankbalancer.data.flight

import com.cedric.tankbalancer.data.db.flight.FlightDao
import com.cedric.tankbalancer.data.db.flight.FlightEntity
import com.cedric.tankbalancer.data.db.flight.toDomain
import com.cedric.tankbalancer.data.db.fuelflow.FuelFlowDao
import com.cedric.tankbalancer.data.db.fuelflow.FuelFlowEntity
import com.cedric.tankbalancer.data.db.laptime.LapTimeDao
import com.cedric.tankbalancer.data.db.laptime.LapTimeEntity
import com.cedric.tankbalancer.domain.flight.FlightRepository
import com.cedric.tankbalancer.domain.model.AircraftTank
import com.cedric.tankbalancer.domain.model.Flight
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FlightRepositoryImpl(
    val flightDao: FlightDao,
    val lapTimeDao: LapTimeDao,
    val fuelFlowDao: FuelFlowDao,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : FlightRepository {

    override val currentFlight: Flow<Flight?>
        get() =
            flightDao.getCurrentFlightWithDetails().map { flightWithDetailsEntity ->
                flightWithDetailsEntity ?: return@map null
                flightWithDetailsEntity.toDomain()
            }

    override suspend fun takeOff(initialTank: AircraftTank, initialLeftFuel: Double, initialRightFuel: Double, initialFuelFlow: Double) {
        withContext(dispatcher) {
            val startTimestamp = System.currentTimeMillis()
            val insertedFlightEntityId = flightDao.insertFlight(
                FlightEntity(
                    startTimestamp = startTimestamp,
                    endTimestamp = null,
                    initialLeftFuel = initialLeftFuel,
                    initialRightFuel = initialRightFuel,
                    initialFuelFlow = initialFuelFlow
                )
            )
            lapTimeDao.insertLapTime(
                LapTimeEntity(
                    flightId = insertedFlightEntityId,
                    tank = initialTank,
                    startTimestamp = startTimestamp,
                    endTimestamp = null
                )
            )
            fuelFlowDao.insertFuelFlow(
                FuelFlowEntity(
                    flightId = insertedFlightEntityId,
                    fuelFlow = initialFuelFlow,
                    timestamp = startTimestamp
                )
            )
        }
    }

    override suspend fun lapTime(error: (FlightRepository.LapTimeError?) -> Unit, success: (AircraftTank) -> Unit) {
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

            val newTank = when (currentLapTime.tank) {
                AircraftTank.LEFT -> AircraftTank.RIGHT
                AircraftTank.RIGHT -> AircraftTank.LEFT
            }

            val insertedLapTime = lapTimeDao.insertLapTime(
                LapTimeEntity(
                    flightId = currentLapTime.flightId,
                    tank = newTank,
                    startTimestamp = currentTimeStamp,
                    endTimestamp = null

                )
            )
            when (insertedLapTime) {
                -1L -> {
                    error(FlightRepository.LapTimeError.INSERTION_FAILED)
                }

                else -> success(newTank)
            }
        }
    }

    override suspend fun newFuelFlow(newFuelFlow: Double, error: (FlightRepository.FuelFlowError?) -> Unit) {
        withContext(dispatcher) {
            val currentTimeStamp = System.currentTimeMillis()
            val currentFlight = flightDao.getCurrentFlightSync()

            if (currentFlight == null) {
                error(FlightRepository.FuelFlowError.NO_CURRENT_FLIGHT)
                return@withContext
            }

            fuelFlowDao.insertFuelFlow(
                FuelFlowEntity(
                    flightId = currentFlight.id,
                    fuelFlow = newFuelFlow,
                    timestamp = currentTimeStamp,
                )
            )
        }
    }

    override suspend fun land(onError: () -> Unit, onSuccess: () -> Unit) {
        withContext(dispatcher) {
            val landingTimestamp = System.currentTimeMillis()

            val currentFlight = flightDao.getCurrentFlightSync()
            if (currentFlight == null) {
                onError()
                return@withContext
            }
            flightDao.updateFlight(
                currentFlight.copy(
                    endTimestamp = landingTimestamp
                )
            )

            val currentLapTime = lapTimeDao.getCurrentLapTimeSync()
            if (currentLapTime == null) {
                onError()
                return@withContext
            }
            lapTimeDao.updateLapTime(
                currentLapTime.copy(
                    endTimestamp = landingTimestamp
                )
            )
            onSuccess()
        }
    }
}
