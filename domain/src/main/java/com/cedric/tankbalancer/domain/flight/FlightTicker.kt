package com.cedric.tankbalancer.domain.flight

import kotlinx.coroutines.flow.Flow

interface FlightTicker {
    val ticker: Flow<Unit>

    fun startTick()

    fun stopTick()
}
