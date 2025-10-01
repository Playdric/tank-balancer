package com.cedric.tankbalancer.presentation.screen.launcher

import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry

data class LauncherUiState(
    val navigationEvent: TankBalancerNavEntry? = null,
    val isLoading: Boolean = true,
    val showRestorePreviousStatePopup: Boolean = false,
)
