package com.cedric.domain.di

import com.cedric.domain.usecase.RemainingFuelUseCase
import com.cedric.domain.usecase.TotalTankTimeUseCase
import org.koin.dsl.module

val domainModule = module {
    single { RemainingFuelUseCase() }
    single { TotalTankTimeUseCase() }
}
