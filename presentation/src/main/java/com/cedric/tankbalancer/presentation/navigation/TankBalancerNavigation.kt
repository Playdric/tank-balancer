package com.cedric.tankbalancer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.cedric.tankbalancer.presentation.screen.balancer.BalancerScreen
import com.cedric.tankbalancer.presentation.screen.setup.SetupScreen
import timber.log.Timber

@Composable
fun TankBalancerNavigation() {
    val backStack = remember { mutableStateListOf<TankBalancerNavEntry>(TankBalancerNavEntry.SetupScreen) }
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
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
