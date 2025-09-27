package com.cedric.data.db.fuelflow

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.cedric.data.db.flight.Flight

@Entity(
    tableName = "FuelFlow",
    foreignKeys = [
        ForeignKey(
            entity = Flight::class,
            parentColumns = ["id"],
            childColumns = ["flightId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class FuelFlow(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val flightId: Long,
    val fuelFlow: Double,
    val timestamp: Long
)
