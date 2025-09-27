package com.cedric.data.db.laptime

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.cedric.data.db.flight.Flight
import com.cedric.domain.model.AircraftTank

@Entity(tableName = "LapTime",
    foreignKeys = [
        ForeignKey(
            entity = Flight::class,
            parentColumns = ["id"],
            childColumns = ["flightId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class LapTime(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val flightId: Long,
    val tank: AircraftTank,
    val startTimestamp: Long,
    val endTimestamp: Long?
)
