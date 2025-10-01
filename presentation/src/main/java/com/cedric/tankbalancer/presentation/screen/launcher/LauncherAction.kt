package com.cedric.tankbalancer.presentation.screen.launcher

sealed interface LauncherAction {
    data class RestorePopupAction(val restorePreviousState: Boolean) : LauncherAction
}
