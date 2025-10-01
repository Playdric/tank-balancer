package com.cedric.tankbalancer.presentation.screen.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices.PIXEL
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.cedric.tankbalancer.presentation.R
import com.cedric.tankbalancer.presentation.composable.ErrorDialog
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import com.cedric.tankbalancer.presentation.theme.TankBalancerTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = koinViewModel(),
    navigate: (TankBalancerNavEntry) -> Unit = {},
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.navigationEvent.collect { event ->
                navigate(event)
            }
        }
    }

    LauncherScreenContent(uiState, viewModel::onAction)
}

@Composable
fun LauncherScreenContent(uiState: LauncherUiState, action: (LauncherAction) -> Unit) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
        if (uiState.showRestorePreviousStatePopup) {
            ErrorDialog(
                onDismissRequest = { action(LauncherAction.RestorePopupAction(false)) },
                onConfirmation = { action(LauncherAction.RestorePopupAction(true)) },
                dialogTitle = stringResource(R.string.restore_flight_title),
                dialogText = stringResource(R.string.restore_flight_message),
                icon = Icons.Default.Flight,
            )
        }
    }

}

@Preview(device = PIXEL)
@Composable
fun LauncherScreenContentPreview() {
    TankBalancerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LauncherScreenContent(uiState = LauncherUiState(), {})
        }
    }
}
