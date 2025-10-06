package com.cedric.domain.usecase

import com.cedric.tankbalancer.domain.model.AircraftTank
import com.cedric.tankbalancer.domain.model.Flight
import com.cedric.tankbalancer.domain.model.LapTime
import com.cedric.tankbalancer.domain.usecase.TotalTankTimeUseCase
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class TotalTankTimeUseCaseTest {
    private lateinit var totalTankTimeUseCase: TotalTankTimeUseCase
    private lateinit var emptyFlight: Flight

    @Before
    fun setUp() {
        totalTankTimeUseCase = TotalTankTimeUseCase()
        emptyFlight = Flight(
            takeOffTimestamp = 0L,
            initialLeftFuel = 0.0,
            initialRightFuel = 0.0,
            lapTimes = emptyList(),
            fuelFlows = emptyList()
        )
    }

    @Test
    fun `invoke with simple single lap left tank only`() {
        val now = System.currentTimeMillis()
        val currentFlight = emptyFlight.copy(
            lapTimes = listOf(
                LapTime(
                    tank = AircraftTank.LEFT,
                    startTimestamp = now,
                    endTimestamp = now + 100L
                )
            )
        )

        val expected = 100L

        val elapsedTime = totalTankTimeUseCase(currentFlight, AircraftTank.LEFT)

        assertEquals(expected, elapsedTime)
    }

    @Test
    fun `invoke with multiple finished laps should return correct total time`() {
        // Arrange
        val startTime = 1000L
        val oneHour = 1.hours.inWholeMilliseconds
        val thirtyMinutes = 30.minutes.inWholeMilliseconds

        val flight = Flight(
            takeOffTimestamp = startTime,
            lapTimes = listOf(
                // Lap 1 on LEFT tank: 1 hour
                LapTime(tank = AircraftTank.LEFT, startTimestamp = startTime, endTimestamp = startTime + oneHour),
                // Lap on RIGHT tank: 30 minutes (should be ignored for LEFT tank calculation)
                LapTime(tank = AircraftTank.RIGHT, startTimestamp = startTime + oneHour, endTimestamp = startTime + oneHour + thirtyMinutes),
                // Lap 2 on LEFT tank: 30 minutes
                LapTime(
                    tank = AircraftTank.LEFT,
                    startTimestamp = startTime + oneHour + thirtyMinutes,
                    endTimestamp = startTime + oneHour + thirtyMinutes + thirtyMinutes
                )
            ),
            // Other flight properties are not relevant for this use case
            initialLeftFuel = 0.0,
            initialRightFuel = 0.0,
            fuelFlows = emptyList()
        )

        // Act
        val totalLeftTime = totalTankTimeUseCase(currentFlight = flight, tank = AircraftTank.LEFT)

        // Assert
        val expectedTotalTime = oneHour + thirtyMinutes // 1 hour + 30 minutes
        assertEquals(expectedTotalTime, totalLeftTime)
    }

    @Test
    fun `invoke with no laps for the specified tank should return zero`() {
        // Arrange
        val startTime = 1000L
        val oneHour = 1.hours.inWholeMilliseconds
        val flight = Flight(
            takeOffTimestamp = startTime,
            lapTimes = listOf(
                // Only laps for RIGHT tank exist
                LapTime(tank = AircraftTank.RIGHT, startTimestamp = startTime, endTimestamp = startTime + oneHour)
            ),
            initialLeftFuel = 0.0,
            initialRightFuel = 0.0,
            fuelFlows = emptyList()
        )

        // Act
        // We ask for the total time of the LEFT tank
        val totalLeftTime = totalTankTimeUseCase(currentFlight = flight, tank = AircraftTank.LEFT)

        // Assert
        assertEquals(0L, totalLeftTime)
    }

    @Test
    fun `invoke with an empty lap list should return zero`() {
        // Arrange
        val flight = Flight(
            takeOffTimestamp = 1000L,
            lapTimes = emptyList(), // No laps at all
            initialLeftFuel = 0.0,
            initialRightFuel = 0.0,
            fuelFlows = emptyList()
        )

        // Act
        val totalLeftTime = totalTankTimeUseCase(currentFlight = flight, tank = AircraftTank.LEFT)

        // Assert
        assertEquals(0L, totalLeftTime)
    }
}
