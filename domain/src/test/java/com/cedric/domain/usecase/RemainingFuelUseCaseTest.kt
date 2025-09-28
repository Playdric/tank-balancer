package com.cedric.domain.usecase

import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.Flight
import com.cedric.domain.model.FuelFlow
import com.cedric.domain.model.LapTime
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class RemainingFuelUseCaseTest {

    private lateinit var remainingFuelUseCase: RemainingFuelUseCase

    @Before
    fun setUp() {
        remainingFuelUseCase = RemainingFuelUseCase()
    }

    @Test
    fun `simple single lap left tank only`() {
        val tank = AircraftTank.LEFT
        val start = 1_000_000L
        val end = 1_600_000L // 10 minutes later

        val flight = Flight(
            takeOffTimestamp = start,
            initialLeftFuel = 1000.0,
            initialRightFuel = 1000.0,
            lapTimes = listOf(LapTime(tank, start, end)),
            fuelFlows = listOf( // 300 kg/h entire duration
                FuelFlow(300.0, start)
            )
        )

        val result = remainingFuelUseCase(flight)
        // 10min = 1/6h; consumed 300*(1/6)=50
        assertEquals(1000.0 - 50.0, result.left, 0.001)
        assertEquals(1000.0, result.right, 0.001)
    }

    @Test
    fun `switches between left and right tanks`() {

        val start = 1_000_000L
        val mid = 1_300_000L // 5 min each
        val end = 1_600_000L // 10 minutes later

        val flight = Flight(
            takeOffTimestamp = start,
            initialLeftFuel = 1000.0,
            initialRightFuel = 1000.0,
            lapTimes = listOf(
                LapTime(AircraftTank.LEFT, start, mid),
                LapTime(AircraftTank.RIGHT, mid, end)
            ),
            fuelFlows = listOf(
                FuelFlow(120.0, start)
            )
        )

        val result = remainingFuelUseCase(flight)
        // Each lap 5min = 1/12h; consumed 120*(1/12)=10 per tank
        assertEquals(1000.0 - 10.0, result.left, 0.001)
        assertEquals(1000.0 - 10.0, result.right, 0.001)
    }

    @Test
    fun `fuel flow changes mid lap`() {
        val tank = AircraftTank.LEFT
        val start = 1_000_000L
        val change = 1_300_000L // 5 min each
        val end = 1_600_000L // 10 minutes later

        val flight = Flight(
            takeOffTimestamp = start,
            initialLeftFuel = 1000.0,
            initialRightFuel = 1000.0,
            lapTimes = listOf(LapTime(tank, start, end)),
            fuelFlows = listOf(
                FuelFlow(300.0, start),  // 0-5min
                FuelFlow(480.0, change)  // 5-10min
            )
        )

        val result = remainingFuelUseCase(flight)
        // First 5min at 300kg/h = 300*(1/12)=25, next 5min at 480kg/h = 480*(1/12)=40
        val consumed = 25.0 + 40.0
        assertEquals(1000.0 - consumed, result.left, 0.001)
        assertEquals(1000.0, result.right, 0.001)
    }

    @Test
    fun `open lap still running`() {
        val tank = AircraftTank.RIGHT
        val start = System.currentTimeMillis() - 60_000 // 1 min ago
        val now = System.currentTimeMillis()
        val flight = Flight(
            takeOffTimestamp = start,
            initialLeftFuel = 500.0,
            initialRightFuel = 500.0,
            lapTimes = listOf(LapTime(tank, start, null)),
            fuelFlows = listOf(FuelFlow(180.0, start)) // 180kg/h
        )

        val result = remainingFuelUseCase(flight)
        val hours = (now - start) / 3_600_000.0 // ms to hours
        val expectedRight = 500.0 - 180.0 * hours
        assertEquals(500.0, result.left, 0.01)
        assertEquals(expectedRight, result.right, 0.5)
    }

    @Test
    fun `multiple switches of fuel flow and tanks`() {
        val start = 1_000_000L
        val lap1End = 1_150_000L // 2.5 min
        val lap2End = 1_300_000L // 2.5 min after lap1
        val lap3End = 1_500_000L // 3.33 min after lap2

        val flight = Flight(
            takeOffTimestamp = start,
            initialLeftFuel = 1000.0,
            initialRightFuel = 1000.0,
            lapTimes = listOf(
                LapTime(AircraftTank.LEFT, start, lap1End),
                LapTime(AircraftTank.RIGHT, lap1End, lap2End),
                LapTime(AircraftTank.LEFT, lap2End, lap3End)
            ),
            fuelFlows = listOf(
                FuelFlow(180.0, start),         // From start
                FuelFlow(240.0, 1_010_000L),    // Mid lap1
                FuelFlow(300.0, lap1End),       // lap2 starts
                FuelFlow(150.0, lap2End + 5_000) // mid lap3
            )
        )

        val result = remainingFuelUseCase(flight)

        // Calculate expected consumption:
        // lap1 segments:
        //  start to 1_010_000L: 10,000 ms = 2.78 min = 0.0463 h, flow = 180 kg/h
        val lap1Seg1Hours = (1_010_000 - start) / 3_600_000.0
        val lap1Seg1Cons = 180.0 * lap1Seg1Hours
        //  1_010_000L to lap1End: 5,000 ms = 1.39 min = 0.0231 h, flow = 240 kg/h
        val lap1Seg2Hours = (lap1End - 1_010_000) / 3_600_000.0
        val lap1Seg2Cons = 240.0 * lap1Seg2Hours

        // lap2 full duration (lap1End to lap2End): 15,000 ms = 4.17 min = 0.0694 h, flow = 300 kg/h
        val lap2Hours = (lap2End - lap1End) / 3_600_000.0
        val lap2Cons = 300.0 * lap2Hours

        // lap3 segments:
        // lap2End to lap2End+5,000 ms: 5,000ms = 1.39 min = 0.0231 h, flow=300 (previous flow, as flow changes after 5,000ms)
        val lap3Seg1Hours = 5_000 / 3_600_000.0
        val lap3Seg1Cons = 300.0 * lap3Seg1Hours
        // lap2End+5,000 ms to lap3End: 15,000 ms = 4.17 min = 0.0694 h, flow=150 kg/h
        val lap3Seg2Hours = (lap3End - (lap2End + 5_000)) / 3_600_000.0
        val lap3Seg2Cons = 150.0 * lap3Seg2Hours

        val leftConsumed = lap1Seg1Cons + lap1Seg2Cons + lap3Seg1Cons + lap3Seg2Cons
        val rightConsumed = lap2Cons

        assertEquals(1000.0 - leftConsumed, result.left, 0.01)
        assertEquals(1000.0 - rightConsumed, result.right, 0.01)
    }
}
