package com.cedric.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cedric.data.db.flight.Flight
import com.cedric.data.db.flight.FlightDao
import com.cedric.data.db.fuelflow.FuelFlow
import com.cedric.data.db.fuelflow.FuelFlowDao
import com.cedric.data.db.laptime.LapTime
import com.cedric.data.db.laptime.LapTimeDao

@Database(entities = [Flight::class, FuelFlow::class, LapTime::class], version = 1)
abstract class BalancerDatabase: RoomDatabase() {

    abstract fun flightDao(): FlightDao
    abstract fun fuelFlowDao(): FuelFlowDao
    abstract fun lapTimeDao(): LapTimeDao


}
