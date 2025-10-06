package com.cedric.tankbalancer.presentation.navigation

import android.os.Parcelable
import com.cedric.tankbalancer.domain.model.AircraftTank
import kotlinx.parcelize.Parcelize

sealed interface TankBalancerNavEntry {

    @Parcelize
    data object LauncherScreen : TankBalancerNavEntry, Parcelable

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

    @Parcelize
    data object SetupScreen : TankBalancerNavEntry, Parcelable
}
