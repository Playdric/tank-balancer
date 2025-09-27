package com.cedric.data.db.laptime

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cedric.data.db.flight.Flight
import com.cedric.data.db.fuelflow.FuelFlow
import kotlinx.coroutines.flow.Flow

@Dao
interface LapTimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLapTime(lapTime: LapTime): Long

    @Update
    suspend fun updateLapTime(lapTime: LapTime)

    @Delete
    suspend fun deleteLapTime(lapTime: LapTime)

    @Query("SELECT * FROM LapTime WHERE id = :id")
    fun getLapTimeById(id: Int): Flow<LapTime?>

    @Query("SELECT * FROM LapTime WHERE endTimestamp IS NULL LIMIT 1")
    fun getCurrentLapTime(): Flow<LapTime?>

    @Query("SELECT * FROM LapTime WHERE endTimestamp IS NULL LIMIT 1")
    fun getCurrentLapTimeSync(): LapTime?
}
