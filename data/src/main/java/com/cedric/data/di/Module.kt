package com.cedric.data.di

import androidx.room.Room
import com.cedric.data.db.BalancerDatabase
import com.cedric.data.db.flight.FlightDao
import com.cedric.data.flight.FlightRepositoryImpl
import com.cedric.domain.flight.FlightRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import kotlin.jvm.java

val dataModule = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            BalancerDatabase::class.java,
            "balancer_database"
        ).build()
    }

    factory {
        val database = get<BalancerDatabase>()
        database.flightDao()
        database.fuelFlowDao()
        database.lapTimeDao()
    }

    single { get<BalancerDatabase>().flightDao() }
    single { get<BalancerDatabase>().fuelFlowDao() }
    single { get<BalancerDatabase>().lapTimeDao() }

    single { FlightRepositoryImpl(get(), get()) as FlightRepository }
}

