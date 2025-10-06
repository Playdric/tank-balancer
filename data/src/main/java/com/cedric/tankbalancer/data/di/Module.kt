package com.cedric.tankbalancer.data.di

import androidx.room.Room
import com.cedric.tankbalancer.data.db.BalancerDatabase
import com.cedric.tankbalancer.data.flight.FlightRepositoryImpl
import com.cedric.tankbalancer.data.flight.FlightTickerImpl
import com.cedric.tankbalancer.domain.flight.FlightRepository
import com.cedric.tankbalancer.domain.flight.FlightTicker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

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

    single { FlightRepositoryImpl(get(), get(), get()) as FlightRepository }
    single { FlightTickerImpl() as FlightTicker }
}

