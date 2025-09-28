package com.cedric.domain.usecase

import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.Flight
import com.cedric.domain.model.RemainingFuel
import kotlin.time.Duration.Companion.hours

class RemainingFuelUseCase() {

    operator fun invoke(currentFlight: Flight): RemainingFuel {
        var leftRemaining = currentFlight.initialLeftFuel
        var rightRemaining = currentFlight.initialRightFuel
        val now = System.currentTimeMillis()

        val lapsWithWindows = currentFlight.lapTimes.map { lap ->
            val start = lap.startTimestamp
            val end = lap.endTimestamp ?: now
            Triple(lap.tank, start, end)
        }

        // Sort fuel flows once outside loop
        val allFlowsSorted = currentFlight.fuelFlows.sortedBy { it.timestamp }

        for ((tank, windowStart, windowEnd) in lapsWithWindows) {
            // Find flow active at or just before windowStart
            val startFlowIndex = allFlowsSorted.indexOfLast { it.timestamp <= windowStart }
            if (startFlowIndex == -1) continue // no fuel flow available

            var flowIndex = startFlowIndex
            var currentSegmentStart = windowStart

            while (currentSegmentStart < windowEnd && flowIndex < allFlowsSorted.size) {
                val currentFlow = allFlowsSorted[flowIndex]

                // segmentEnd is next flow timestamp or lap end whichever is smaller
                val nextFlowTimestamp = if (flowIndex + 1 < allFlowsSorted.size) allFlowsSorted[flowIndex + 1].timestamp else Long.MAX_VALUE
                val segmentEnd = minOf(nextFlowTimestamp, windowEnd)

                val hours = (segmentEnd - currentSegmentStart) / 1.hours.inWholeMilliseconds.toDouble()
                val fuelConsumed = currentFlow.fuelFlow * hours

                when (tank) {
                    AircraftTank.LEFT -> leftRemaining -= fuelConsumed
                    AircraftTank.RIGHT -> rightRemaining -= fuelConsumed
                }

                currentSegmentStart = segmentEnd
                flowIndex++
            }
        }
        return RemainingFuel(left = leftRemaining, right = rightRemaining)
    }
}
