package com.cedric.tankbalancer.presentation.screen.balancer

sealed interface BalancerAction {
    data object TakeOff: BalancerAction
    data object AcknowledgeError: BalancerAction
}
