package com.cedric.tankbalancer.presentation.screen.balancer

import com.cedric.domain.model.AircraftTank

sealed interface BalancerAction {
    data class TakeOff(
        val initialFuelLeft: Double,
        val initialFuelRight: Double,
        val initialFuelFlow: Double,
        val initialTank: AircraftTank,
    ) : BalancerAction
    data object IncreaseFuelFlow : BalancerAction
    data object DecreaseFuelFlow : BalancerAction
    data object ValidFuelFlow : BalancerAction
    data object SwitchTank : BalancerAction
    data object ConfirmLanding : BalancerAction
}
