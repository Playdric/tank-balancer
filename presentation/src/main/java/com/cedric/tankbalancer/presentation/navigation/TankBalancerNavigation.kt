package com.cedric.tankbalancer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.cedric.tankbalancer.presentation.screen.balancer.BalancerScreen
import com.cedric.tankbalancer.presentation.screen.launcher.LauncherScreen
import com.cedric.tankbalancer.presentation.screen.setup.SetupScreen

@Composable
fun TankBalancerNavigation() {
    val backStack = rememberSaveable { mutableStateListOf<TankBalancerNavEntry>(TankBalancerNavEntry.LauncherScreen) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->

            when (key) {
                TankBalancerNavEntry.LauncherScreen -> NavEntry(key) {
                    LauncherScreen(
                        navigate = { element ->
                            backStack.removeLastOrNull()
                            backStack.add(element)
                        }
                    )
                }

                is TankBalancerNavEntry.BalancerScreen -> NavEntry(key) {
                    BalancerScreen(
                        arguments = key.arguments,
                        navigate = { element -> backStack.add(element) }
                    )
                }

                TankBalancerNavEntry.SetupScreen -> NavEntry(key) {
                    SetupScreen(
                        navigate = { element ->
                            backStack.removeLastOrNull()
                            backStack.add(element)
                        }
                    )
                }
            }
        },
    )
}
