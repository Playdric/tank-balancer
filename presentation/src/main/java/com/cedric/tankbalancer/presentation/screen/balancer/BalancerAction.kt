package com.cedric.tankbalancer.presentation.screen.balancer

sealed interface BalancerAction {
    data object TakeOff: BalancerAction
    data object IncreaseFuelFlow : BalancerAction
    data object DecreaseFuelFlow : BalancerAction
    data object ValidFuelFlow : BalancerAction
    data object SwitchTank : BalancerAction
    data object ConfirmLanding : BalancerAction
    data object AcknowledgeError: BalancerAction
}
