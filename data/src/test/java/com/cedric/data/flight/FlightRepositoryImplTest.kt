package com.cedric.data.flight

import com.cedric.tankbalancer.data.db.flight.FlightDao
import com.cedric.tankbalancer.data.db.flight.FlightEntity
import com.cedric.tankbalancer.data.db.fuelflow.FuelFlowDao
import com.cedric.tankbalancer.data.db.fuelflow.FuelFlowEntity
import com.cedric.tankbalancer.data.db.laptime.LapTimeDao
import com.cedric.tankbalancer.data.db.laptime.LapTimeEntity
import com.cedric.tankbalancer.data.flight.FlightRepositoryImpl
import com.cedric.tankbalancer.domain.flight.FlightRepository
import com.cedric.tankbalancer.domain.model.AircraftTank
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlightRepositoryImplTest {

    // Mocks for dependencies
    private lateinit var flightDao: FlightDao
    private lateinit var lapTimeDao: LapTimeDao
    private lateinit var fuelFlowDao: FuelFlowDao
    private lateinit var flightRepository: FlightRepositoryImpl

    @Before
    fun setUp() {
        // Initialize mocks before each test
        flightDao = mockk(relaxed = true)
        lapTimeDao = mockk(relaxed = true)
        fuelFlowDao = mockk(relaxed = true)

        flightRepository = FlightRepositoryImpl(
            flightDao = flightDao,
            lapTimeDao = lapTimeDao,
            fuelFlowDao = fuelFlowDao,
            dispatcher = UnconfinedTestDispatcher() // Use a simple test dispatcher
        )
    }

    @Test
    fun `takeOff should insert flight, initial lap time, and initial fuel flow`() = runTest {
        // Given
        val initialLeftFuel = 100.0
        val initialRightFuel = 100.0
        val initialFuelFlow = 20.0
        val expectedFlightId = 1L

        val flightEntitySlot = slot<FlightEntity>()
        val lapTimeEntitySlot = slot<LapTimeEntity>()
        val fuelFlowEntitySlot = slot<FuelFlowEntity>()

        coEvery { flightDao.insertFlight(capture(flightEntitySlot)) } returns expectedFlightId

        // When
        flightRepository.takeOff(
            initialTank = AircraftTank.LEFT,
            initialLeftFuel = initialLeftFuel,
            initialRightFuel = initialRightFuel,
            initialFuelFlow = initialFuelFlow
        )

        // Then
        coVerify(exactly = 1) { flightDao.insertFlight(any()) }
        coVerify(exactly = 1) { lapTimeDao.insertLapTime(capture(lapTimeEntitySlot)) }
        coVerify(exactly = 1) { fuelFlowDao.insertFuelFlow(capture(fuelFlowEntitySlot)) }

        // Assert flight data
        val capturedFlight = flightEntitySlot.captured
        assertEquals(initialLeftFuel, capturedFlight.initialLeftFuel, 0.0)
        assertEquals(initialRightFuel, capturedFlight.initialRightFuel, 0.0)
        assertEquals(initialFuelFlow, capturedFlight.initialFuelFlow, 0.0)
        assertNull(capturedFlight.endTimestamp)

        // Assert lap time data
        val capturedLapTime = lapTimeEntitySlot.captured
        assertEquals(expectedFlightId, capturedLapTime.flightId)
        assertEquals(AircraftTank.LEFT, capturedLapTime.tank)
        assertNull(capturedLapTime.endTimestamp)

        // Assert fuel flow data
        val capturedFuelFlow = fuelFlowEntitySlot.captured
        assertEquals(expectedFlightId, capturedFuelFlow.flightId)
        assertEquals(initialFuelFlow, capturedFuelFlow.fuelFlow, 0.0)
    }

    @Test
    fun `lapTime should invoke error callback with NO_CURRENT_FLIGHT`() = runTest {
        // Given
        coEvery { flightDao.getCurrentFlightSync() } returns null
        var receivedError: FlightRepository.LapTimeError? = null
        var successCalled = false

        // When
        flightRepository.lapTime(
            error = { error -> receivedError = error },
            success = { successCalled = true }
        )

        // Then
        assertEquals(FlightRepository.LapTimeError.NO_CURRENT_FLIGHT, receivedError)
        assertEquals(false, successCalled)
    }

    @Test
    fun `lapTime should invoke error callback with NO_CURRENT_LAP_TIME`() = runTest {
        // Given
        coEvery { flightDao.getCurrentFlightSync() } returns mockk()
        coEvery { lapTimeDao.getCurrentLapTimeSync() } returns null
        var receivedError: FlightRepository.LapTimeError? = null
        var successCalled = false

        // When
        flightRepository.lapTime(
            error = { error -> receivedError = error },
            success = { successCalled = true }
        )

        // Then
        assertEquals(FlightRepository.LapTimeError.NO_CURRENT_LAP_TIME, receivedError)
        assertEquals(false, successCalled)
    }

    @Test
    fun `lapTime should update current lap, insert new one, and call success`() = runTest {
        // Given
        val flightId = 1L
        val currentFlightEntity = FlightEntity(
            id = flightId,
            startTimestamp = 1000L,
            endTimestamp = null,
            initialLeftFuel = 100.0,
            initialRightFuel = 100.0,
            initialFuelFlow = 20.0
        )
        val currentLapTimeEntity = LapTimeEntity(id = 10L, flightId = flightId, tank = AircraftTank.LEFT, startTimestamp = 1000L, endTimestamp = null)
        coEvery { flightDao.getCurrentFlightSync() } returns currentFlightEntity
        coEvery { lapTimeDao.getCurrentLapTimeSync() } returns currentLapTimeEntity
        coEvery { lapTimeDao.insertLapTime(any()) } returns 11L // Simulate successful insertion

        val updatedLapSlot = slot<LapTimeEntity>()
        val newLapSlot = slot<LapTimeEntity>()
        var receivedError: FlightRepository.LapTimeError? = null
        var newTank: AircraftTank? = null

        // When
        flightRepository.lapTime(
            error = { err -> receivedError = err },
            success = { tank -> newTank = tank }
        )

        // Then
        assertNull("No error should be emitted", receivedError)
        assertEquals("Success should be called with the new tank", AircraftTank.RIGHT, newTank)

        coVerify { lapTimeDao.updateLapTime(capture(updatedLapSlot)) }
        coVerify { lapTimeDao.insertLapTime(capture(newLapSlot)) }

        val capturedUpdate = updatedLapSlot.captured
        assertNotNull("Old lap should have an end timestamp", capturedUpdate.endTimestamp)

        val capturedNew = newLapSlot.captured
        assertEquals(AircraftTank.RIGHT, capturedNew.tank)
        assertNull("New lap should not have an end timestamp", capturedNew.endTimestamp)
        assertEquals(capturedUpdate.endTimestamp, capturedNew.startTimestamp)
    }

    @Test
    fun `newFuelFlow should invoke error when no current flight`() = runTest {
        // Given
        coEvery { flightDao.getCurrentFlightSync() } returns null
        var receivedError: FlightRepository.FuelFlowError? = null

        // When
        flightRepository.newFuelFlow(25.0, error = { err -> receivedError = err })

        // Then
        assertEquals(FlightRepository.FuelFlowError.NO_CURRENT_FLIGHT, receivedError)
        coVerify(exactly = 0) { fuelFlowDao.insertFuelFlow(any()) }
    }

    @Test
    fun `newFuelFlow should insert new fuel flow when flight exists`() = runTest {
        // Given
        val flightId = 1L
        coEvery { flightDao.getCurrentFlightSync() } returns FlightEntity(
            id = flightId,
            startTimestamp = 1000L,
            endTimestamp = null,
            initialLeftFuel = 100.0,
            initialRightFuel = 100.0,
            initialFuelFlow = 20.0
        )
        val fuelFlowSlot = slot<FuelFlowEntity>()
        var receivedError: FlightRepository.FuelFlowError? = null

        // When
        flightRepository.newFuelFlow(25.0, error = { err -> receivedError = err })

        // Then
        assertNull("No error should be emitted", receivedError)
        coVerify(exactly = 1) { fuelFlowDao.insertFuelFlow(capture(fuelFlowSlot)) }
        assertEquals(flightId, fuelFlowSlot.captured.flightId)
        assertEquals(25.0, fuelFlowSlot.captured.fuelFlow, 0.0)
    }

    @Test
    fun `land should call onSuccess when flight and lap exist`() = runTest {
        // Given
        coEvery { flightDao.getCurrentFlightSync() } returns mockk(relaxed = true)
        coEvery { lapTimeDao.getCurrentLapTimeSync() } returns mockk(relaxed = true)
        var successCalled = false
        var errorCalled = false

        // When
        flightRepository.land(onError = { errorCalled = true }, onSuccess = { successCalled = true })

        // Then
        assertEquals(true, successCalled)
        assertEquals(false, errorCalled)
        coVerify(exactly = 1) { flightDao.updateFlight(any()) }
        coVerify(exactly = 1) { lapTimeDao.updateLapTime(any()) }
    }

    @Test
    fun `land should call onError when no current flight`() = runTest {
        // Given
        coEvery { flightDao.getCurrentFlightSync() } returns null
        var successCalled = false
        var errorCalled = false

        // When
        flightRepository.land(onError = { errorCalled = true }, onSuccess = { successCalled = true })

        // Then
        assertEquals(false, successCalled)
        assertEquals(true, errorCalled)
        coVerify(exactly = 0) { flightDao.updateFlight(any()) }
        coVerify(exactly = 0) { lapTimeDao.updateLapTime(any()) }
    }
}
