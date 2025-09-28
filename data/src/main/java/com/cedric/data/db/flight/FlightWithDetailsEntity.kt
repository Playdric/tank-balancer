package com.cedric.data.db.flight

import androidx.room.Embedded
import androidx.room.Relation
import com.cedric.data.db.fuelflow.FuelFlowEntity
import com.cedric.data.db.fuelflow.toDomain
import com.cedric.data.db.laptime.LapTimeEntity
import com.cedric.data.db.laptime.toDomain
import com.cedric.domain.model.Flight

data class FlightWithDetailsEntity(
    @Embedded val flight: FlightEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "flightId"
    )
    val lapTimes: List<LapTimeEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "flightId"
    )
    val fuelFlows: List<FuelFlowEntity>
)

fun FlightWithDetailsEntity.toDomain() = Flight(
    takeOffTimestamp = flight.startTimestamp,
    initialLeftFuel = flight.initialLeftFuel,
    initialRightFuel = flight.initialRightFuel,
    lapTimes = lapTimes.map { it.toDomain() },
    fuelFlows = fuelFlows.map { it.toDomain() },
)

