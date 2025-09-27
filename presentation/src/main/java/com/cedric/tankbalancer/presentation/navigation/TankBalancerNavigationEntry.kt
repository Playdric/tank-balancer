package com.cedric.tankbalancer.presentation.navigation

import com.cedric.domain.model.AircraftTank


sealed interface TankBalancerNavEntry {

    data class BalancerScreen(val arguments: Arguments) : TankBalancerNavEntry {
        data class Arguments(
            val initialFuelLeft: Double,
            val initialFuelRight: Double,
            val initialFuelFlow: Double,
            val initialTank: AircraftTank,
        )
    }

    data object SetupScreen : TankBalancerNavEntry
}
