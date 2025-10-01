package com.cedric.tankbalancer.presentation.navigation

import android.os.Parcelable
import com.cedric.domain.model.AircraftTank
import kotlinx.parcelize.Parcelize

sealed interface TankBalancerNavEntry {

    data object LauncherScreen : TankBalancerNavEntry

    @Parcelize
    data class BalancerScreen(val arguments: Arguments? = null) : TankBalancerNavEntry, Parcelable {
        //TODO rework this, I don't like it
        @Parcelize
        data class Arguments(
            val initialFuelLeft: Double,
            val initialFuelRight: Double,
            val initialFuelFlow: Double,
            val initialTank: AircraftTank,
        ) : Parcelable
    }

    data object SetupScreen : TankBalancerNavEntry
}
