package com.cedric.tankbalancer.data.db.flight

import androidx.room.Embedded
import androidx.room.Relation
import com.cedric.tankbalancer.data.db.fuelflow.FuelFlowEntity
import com.cedric.tankbalancer.data.db.fuelflow.toDomain
import com.cedric.tankbalancer.data.db.laptime.LapTimeEntity
import com.cedric.tankbalancer.data.db.laptime.toDomain
import com.cedric.tankbalancer.domain.model.Flight

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

