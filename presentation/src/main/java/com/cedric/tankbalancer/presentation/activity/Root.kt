package com.cedric.tankbalancer.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavigation


@Composable
fun TankBalancerApp(innerPadding: PaddingValues) {
    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
        TankBalancerNavigation()
    }
}
