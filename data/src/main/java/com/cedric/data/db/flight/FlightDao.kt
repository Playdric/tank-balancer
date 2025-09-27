package com.cedric.data.db.flight

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlight(flight: Flight): Long

    @Update
    suspend fun updateFlight(flight: Flight)

    @Delete
    suspend fun deleteFlight(flight: Flight)

    @Query("SELECT * FROM Flight WHERE id = :id")
    fun getFlightById(id: Int): Flow<Flight?>

    @Query("SELECT * FROM Flight WHERE endTimestamp IS NULL LIMIT 1")
    fun getCurrentFlight(): Flow<Flight?>

    @Query("SELECT * FROM Flight WHERE endTimestamp IS NULL LIMIT 1")
    fun getCurrentFlightSync(): Flight?
}
