package com.cedric.data.db.fuelflow

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.cedric.data.db.flight.FlightEntity
import com.cedric.domain.model.FuelFlow

@Entity(
    tableName = "FuelFlow",
    foreignKeys = [
        ForeignKey(
            entity = FlightEntity::class,
            parentColumns = ["id"],
            childColumns = ["flightId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class FuelFlowEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val flightId: Long,
    val fuelFlow: Double,
    val timestamp: Long
)

fun FuelFlowEntity.toDomain(): FuelFlow = FuelFlow(
    fuelFlow = fuelFlow,
    timestamp = timestamp
)
