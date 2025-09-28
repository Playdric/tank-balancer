package com.cedric.data.db.laptime

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.cedric.data.db.flight.FlightEntity
import com.cedric.domain.model.AircraftTank
import com.cedric.domain.model.LapTime

@Entity(tableName = "LapTime",
    foreignKeys = [
        ForeignKey(
            entity = FlightEntity::class,
            parentColumns = ["id"],
            childColumns = ["flightId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class LapTimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val flightId: Long,
    val tank: AircraftTank,
    val startTimestamp: Long,
    val endTimestamp: Long?
)

fun LapTimeEntity.toDomain(): LapTime = LapTime(
    tank = tank,
    startTimestamp = startTimestamp,
    endTimestamp = endTimestamp
)
