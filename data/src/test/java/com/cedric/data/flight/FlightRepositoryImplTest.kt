package com.cedric.data.flight

import com.cedric.data.db.flight.Flight
import com.cedric.data.db.flight.FlightDao
import com.cedric.data.db.laptime.AircraftTank
import com.cedric.data.db.laptime.LapTime
import com.cedric.data.db.laptime.LapTimeDao
import com.cedric.domain.flight.FlightRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class FlightRepositoryImplTest {

    // Mocks for dependencies
    private lateinit var flightDao: FlightDao
    private lateinit var lapTimeDao: LapTimeDao

    @Before
    fun setUp() {
        // Initialize mocks before each test
        flightDao = mockk<FlightDao>(relaxed = true)
        lapTimeDao = mockk<LapTimeDao>(relaxed = true)

    }

    @Test
    fun `takeOff should insert a new flight and an initial left lap time`() = runTest {
        val flightRepository = FlightRepositoryImpl(
            flightDao = flightDao,
            lapTimeDao = lapTimeDao,
            dispatcher =  UnconfinedTestDispatcher(testScheduler)
        )
        // Given
        val initialLeftFuel = 100.0
        val initialRightFuel = 100.0
        val initialFuelFlow = 20.0
        val expectedFlightId = 1L

        // We capture the flight passed to the DAO to verify its contents
        val flightSlot = slot<Flight>()
        coEvery { flightDao.insertFlight(capture(flightSlot)) } returns expectedFlightId

        val lapTimeSlot = slot<LapTime>()
        coEvery { lapTimeDao.insertLapTime(capture(lapTimeSlot)) } returns 1L

        // When
        flightRepository.takeOff(initialLeftFuel, initialRightFuel, initialFuelFlow)

        // Then
        // Verify that the DAOs were called within a transaction
        coVerify { flightDao.insertFlight(any()) }
        coVerify { lapTimeDao.insertLapTime(any()) }

        // Assert the captured flight data is correct
        val capturedFlight = flightSlot.captured
        assertEquals(initialLeftFuel, capturedFlight.initialLeftFuel, 0.0)
        assertEquals(initialRightFuel, capturedFlight.initialRightFuel, 0.0)
        assertEquals(initialFuelFlow, capturedFlight.initialFuelFlow, 0.0)
        assertNull(capturedFlight.endTimestamp)

        // Assert the captured lap time data is correct
        val capturedLapTime = lapTimeSlot.captured
        assertEquals(expectedFlightId, capturedLapTime.flightId)
        assertEquals(AircraftTank.LEFT, capturedLapTime.tank)
        assertNull(capturedLapTime.endTimestamp)
    }

    @Test
    fun `lapTime should emit NO_CURRENT_FLIGHT error when no active flight`() = runTest {
        val flightRepository = FlightRepositoryImpl(
            flightDao = flightDao,
            lapTimeDao = lapTimeDao,
            dispatcher = UnconfinedTestDispatcher(testScheduler)
        )
        // Given
        // No current flight exists
        coEvery { flightDao.getCurrentFlightSync() } returns null
        var receivedError: FlightRepository.LapTimeError? = null

        // When
        flightRepository.lapTime { error -> receivedError = error }

        // Then
        // Verify that the error callback was invoked with the correct error
        assertEquals(FlightRepository.LapTimeError.NO_CURRENT_FLIGHT, receivedError)

        // Verify that no lap times were updated or inserted
        coVerify(exactly = 0) { lapTimeDao.updateLapTime(any()) }
        coVerify(exactly = 0) { lapTimeDao.insertLapTime(any()) }
    }

    @Test
    fun `lapTime should emit NO_CURRENT_LAP_TIME error when no active lap time`() = runTest {
        val flightRepository = FlightRepositoryImpl(
            flightDao = flightDao,
            lapTimeDao = lapTimeDao,
            dispatcher = UnconfinedTestDispatcher(testScheduler)
        )
        // Given
        // A current flight exists, but no current lap time
        val currentFlight =
            Flight(id = 1L, startTimestamp = 1000L, endTimestamp = null, initialLeftFuel = 100.0, initialRightFuel = 100.0, initialFuelFlow = 20.0)
        coEvery { flightDao.getCurrentFlightSync() } returns currentFlight
        coEvery { lapTimeDao.getCurrentLapTimeSync() } returns null
        var receivedError: FlightRepository.LapTimeError? = null

        // When
        flightRepository.lapTime { error -> receivedError = error }

        // Then
        // Verify that the error callback was invoked with the correct error
        assertEquals(FlightRepository.LapTimeError.NO_CURRENT_LAP_TIME, receivedError)
        coVerify(exactly = 0) { lapTimeDao.updateLapTime(any()) }
        coVerify(exactly = 0) { lapTimeDao.insertLapTime(any()) }
    }

    @Test
    fun `lapTime should update current lap and insert a new one for the other tank`() = runTest {
        val flightRepository = FlightRepositoryImpl(
            flightDao = flightDao,
            lapTimeDao = lapTimeDao,
            dispatcher = UnconfinedTestDispatcher(testScheduler)
        )
        // Given
        val flightId = 1L
        val currentFlight = Flight(
            id = flightId,
            startTimestamp = 1000L,
            endTimestamp = null,
            initialLeftFuel = 100.0,
            initialRightFuel = 100.0,
            initialFuelFlow = 20.0
        )
        val currentLapTime = LapTime(id = 10L, flightId = flightId, tank = AircraftTank.LEFT, startTimestamp = 1000L, endTimestamp = null)

        coEvery { flightDao.getCurrentFlightSync() } returns currentFlight
        coEvery { lapTimeDao.getCurrentLapTimeSync() } returns currentLapTime

        val updatedLapTimeSlot = slot<LapTime>()
        coEvery { lapTimeDao.updateLapTime(capture(updatedLapTimeSlot)) } returns Unit

        val newLapTimeSlot = slot<LapTime>()
        coEvery { lapTimeDao.insertLapTime(capture(newLapTimeSlot)) } returns 11L

        var receivedError: FlightRepository.LapTimeError? = null

        // When
        flightRepository.lapTime { error -> receivedError = error }

        // Then
        assertNull("No error should be emitted", receivedError)

        // Verify DAO calls
        coVerify { lapTimeDao.updateLapTime(any()) }
        coVerify { lapTimeDao.insertLapTime(any()) }

        // Assert that the current lap time was updated correctly (it should have an end timestamp)
        val capturedUpdate = updatedLapTimeSlot.captured
        assertEquals(currentLapTime.id, capturedUpdate.id)
        assertEquals(currentLapTime.flightId, capturedUpdate.flightId)
        assertEquals(currentLapTime.tank, capturedUpdate.tank)
        assertEquals(currentLapTime.startTimestamp, capturedUpdate.startTimestamp)
        // Check that an end timestamp was set
        assert(capturedUpdate.endTimestamp != null)

        // Assert that the new lap time was created for the other tank
        val capturedNewLap = newLapTimeSlot.captured
        assertEquals(flightId, capturedNewLap.flightId)
        assertEquals(AircraftTank.RIGHT, capturedNewLap.tank) // Switched from LEFT to RIGHT
        assertNull(capturedNewLap.endTimestamp)
        assertEquals(capturedUpdate.endTimestamp, capturedNewLap.startTimestamp) // Start time of new lap is end time of old lap
    }
}
