package com.cedric.tankbalancer.domain.di

import com.cedric.tankbalancer.domain.usecase.RemainingFuelUseCase
import com.cedric.tankbalancer.domain.usecase.TotalTankTimeUseCase
import org.koin.dsl.module

val domainModule = module {
    single { RemainingFuelUseCase() }
    single { TotalTankTimeUseCase() }
}
