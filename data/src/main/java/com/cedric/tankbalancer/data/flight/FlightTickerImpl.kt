package com.cedric.tankbalancer.data.flight

import com.cedric.tankbalancer.domain.flight.FlightTicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class FlightTickerImpl(val dispatcher: CoroutineContext = Dispatchers.Default) : FlightTicker {

    private var _ticker = MutableSharedFlow<Unit>()
    override val ticker = _ticker.asSharedFlow()

    private var job: Job? = null

    override fun startTick() {
        job = CoroutineScope(dispatcher).launch {
            _ticker.emit(Unit) // start at 0
            while (this.isActive) {
                delay(1000)
                _ticker.emit(Unit)
            }
        }
    }

    override fun stopTick() {
        job?.cancel()
        job = null
    }

}
