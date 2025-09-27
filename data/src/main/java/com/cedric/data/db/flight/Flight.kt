package com.cedric.data.db.flight

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Flight")
data class Flight(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimestamp: Long,
    val endTimestamp: Long?,
    val initialLeftFuel: Double,
    val initialRightFuel: Double,
    val initialFuelFlow: Double,
)
