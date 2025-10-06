package com.cedric.tankbalancer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cedric.tankbalancer.data.db.flight.FlightDao
import com.cedric.tankbalancer.data.db.flight.FlightEntity
import com.cedric.tankbalancer.data.db.fuelflow.FuelFlowDao
import com.cedric.tankbalancer.data.db.fuelflow.FuelFlowEntity
import com.cedric.tankbalancer.data.db.laptime.LapTimeDao
import com.cedric.tankbalancer.data.db.laptime.LapTimeEntity

@Database(entities = [FlightEntity::class, FuelFlowEntity::class, LapTimeEntity::class], version = 1)
abstract class BalancerDatabase: RoomDatabase() {

    abstract fun flightDao(): FlightDao
    abstract fun fuelFlowDao(): FuelFlowDao
    abstract fun lapTimeDao(): LapTimeDao


}
