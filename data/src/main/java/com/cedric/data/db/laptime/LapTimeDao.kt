package com.cedric.data.db.laptime

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LapTimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLapTime(lapTimeEntity: LapTimeEntity): Long

    @Update
    suspend fun updateLapTime(lapTimeEntity: LapTimeEntity)

    @Delete
    suspend fun deleteLapTime(lapTimeEntity: LapTimeEntity)

    @Query("SELECT * FROM LapTime WHERE id = :id")
    fun getLapTimeById(id: Int): Flow<LapTimeEntity?>

    @Query("SELECT * FROM LapTime WHERE endTimestamp IS NULL LIMIT 1")
    fun getCurrentLapTime(): Flow<LapTimeEntity?>

    @Query("SELECT * FROM LapTime WHERE endTimestamp IS NULL LIMIT 1")
    fun getCurrentLapTimeSync(): LapTimeEntity?
}
