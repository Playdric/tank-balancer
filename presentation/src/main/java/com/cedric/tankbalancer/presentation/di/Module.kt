package com.cedric.tankbalancer.presentation.di

import com.cedric.tankbalancer.presentation.screen.balancer.BalancerViewModel
import com.cedric.tankbalancer.presentation.screen.setup.SetupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::BalancerViewModel)
    viewModelOf(::SetupViewModel)
}
