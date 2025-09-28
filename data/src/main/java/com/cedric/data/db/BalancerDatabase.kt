package com.cedric.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cedric.data.db.flight.FlightDao
import com.cedric.data.db.flight.FlightEntity
import com.cedric.data.db.fuelflow.FuelFlowDao
import com.cedric.data.db.fuelflow.FuelFlowEntity
import com.cedric.data.db.laptime.LapTimeDao
import com.cedric.data.db.laptime.LapTimeEntity

@Database(entities = [FlightEntity::class, FuelFlowEntity::class, LapTimeEntity::class], version = 1)
abstract class BalancerDatabase: RoomDatabase() {

    abstract fun flightDao(): FlightDao
    abstract fun fuelFlowDao(): FuelFlowDao
    abstract fun lapTimeDao(): LapTimeDao


}
