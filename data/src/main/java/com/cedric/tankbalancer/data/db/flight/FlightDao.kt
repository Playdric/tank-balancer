package com.cedric.tankbalancer.data.db.flight

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlight(flightEntity: FlightEntity): Long

    @Update
    suspend fun updateFlight(flightEntity: FlightEntity)

    @Delete
    suspend fun deleteFlight(flightEntity: FlightEntity)

    @Query("SELECT * FROM Flight WHERE id = :id")
    fun getFlightById(id: Int): Flow<FlightEntity?>

    @Query("SELECT * FROM Flight ORDER BY id DESC LIMIT 1")
    fun getCurrentFlight(): Flow<FlightEntity?>

    @Query("SELECT * FROM Flight ORDER BY id DESC LIMIT 1")
    fun getCurrentFlightSync(): FlightEntity?

    //TODO do not sort by ID but by timestamp to have the real latest for sure
    @Transaction
    @Query("SELECT * FROM Flight ORDER BY id DESC LIMIT 1")
    fun getCurrentFlightWithDetails(): Flow<FlightWithDetailsEntity?>
}
