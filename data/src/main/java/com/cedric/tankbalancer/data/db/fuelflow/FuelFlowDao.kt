package com.cedric.tankbalancer.data.db.fuelflow

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelFlowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelFlow(fuelFlowEntity: FuelFlowEntity)

    @Update
    suspend fun updateFuelFlow(fuelFlowEntity: FuelFlowEntity)

    @Delete
    suspend fun deleteFuelFlow(fuelFlowEntity: FuelFlowEntity)

    @Query("SELECT * FROM FuelFlow WHERE id = :id")
    fun getFuelFlowById(id: Int): Flow<FuelFlowEntity?>
}
