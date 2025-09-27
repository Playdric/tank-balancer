package com.cedric.data.db.fuelflow

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
    suspend fun insertFuelFlow(fuelFlow: FuelFlow)

    @Update
    suspend fun updateFuelFlow(fuelFlow: FuelFlow)

    @Delete
    suspend fun deleteFuelFlow(fuelFlow: FuelFlow)

    @Query("SELECT * FROM FuelFlow WHERE id = :id")
    fun getFuelFlowById(id: Int): Flow<FuelFlow?>
}
