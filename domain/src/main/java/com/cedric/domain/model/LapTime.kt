package com.cedric.domain.model

data class LapTime(
    val tank: AircraftTank,
    val startTimestamp: Long,
    val endTimestamp: Long?
)
