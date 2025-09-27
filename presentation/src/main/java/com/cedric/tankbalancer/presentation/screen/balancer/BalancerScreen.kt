package com.cedric.tankbalancer.presentation.screen.balancer

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_9_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cedric.tankbalancer.presentation.R
import com.cedric.tankbalancer.presentation.composable.Aircraft
import com.cedric.tankbalancer.presentation.composable.ErrorDialog
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import com.cedric.tankbalancer.presentation.theme.TankBalancerTheme
import com.cedric.tankbalancer.presentation.theme.spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel

@Composable
fun BalancerScreen(
    viewModel: BalancerViewModel = koinViewModel(),
    arguments: TankBalancerNavEntry.BalancerScreen.Arguments? = null,
    navigate: (TankBalancerNavEntry) -> Unit = {},
) {
    val balancerUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigationEvent by viewModel.navigationEvent.collectAsStateWithLifecycle(null)

    viewModel.setArguments(arguments)

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { navigate.invoke(it) }
    }

    BalancerScreenContent(uiState = balancerUiState, action = viewModel::onAction)

}

@Composable
fun BalancerScreenContent(uiState: BalancerUiState, action: (BalancerAction) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (uiState.balancerError != null) Modifier.blur(30.dp) else Modifier)
    ) {
        TotalTimes(
            leftTankTotalTime = uiState.leftTankTotalTime,
            totalTime = uiState.totalTime,
            rightTankTotalTime = uiState.rightTankTotalTime,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Aircraft(
                currentTank = uiState.currentTank,
                currentTime = uiState.totalTime,
                leftFuel = uiState.leftTankFuel,
                rightFuel = uiState.rightTankFuel,
                darkTheme = isSystemInDarkTheme(),
            )
        }
        if (!uiState.isFlying) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    action.invoke(BalancerAction.TakeOff)
                }) {
                    Text(stringResource(R.string.take_off))
                }
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        LapTimes(
            lapTimes = persistentListOf(
                "1:20",
                "1:30",
                "1:40",
                "1:50",
                "1:20",
                "1:30",
                "1:40",
                "1:50",
                "1:20",
                "1:30",
                "1:40",
                "1:50",
                "1:20",
                "1:30",
                "1:40",
                "1:50",
                "1:20",
                "1:30",
                "1:40",
                "1:50",
                "1:20",
                "1:30",
                "1:40",
                "1:50",
                "1:20",
                "1:30",
                "1:40",
                "1:50",
                "1:30",
                "1:40",
                "1:50",
                "1:20",
                "1:30",
                "1:40",
                "1:50",
                "1:30",
                "1:40",
                "1:50",
                "1:20",
                "1:30",
                "1:40",
                "1:50",
                "1:30",
                "1:40",
                "1:50",
                "1:20",
                "1:30",
                "1:40",
                "1:50",
            )
        )
    }

    if (uiState.balancerError != null) {
        when (uiState.balancerError) {
            BalancerError.TAKE_OFF_WILE_NOT_SETUP -> {
                ErrorDialog(
                    onDismissRequest = { action.invoke(BalancerAction.AcknowledgeError) },
                    onConfirmation = { action.invoke(BalancerAction.AcknowledgeError) },
                    dialogTitle = "Warning",
                    dialogText = "Cannot Take Off before setup",
                )
            }
        }
    }
}

@Composable
fun TotalTimes(
    leftTankTotalTime: String = "",
    totalTime: String = "",
    rightTankTotalTime: String = "",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1F)
                .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(MaterialTheme.spacing.small)
        ) {
            Row(
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(1F),
                    text = stringResource(R.string.total_time_left),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Row(
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(1F),
                    text = leftTankTotalTime,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1F)
                .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(MaterialTheme.spacing.small)
        ) {
            Row(
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = stringResource(R.string.total_time),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
            Row(
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(1F),
                    text = (totalTime),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1F)
                .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(MaterialTheme.spacing.small)
        ) {
            Row(
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = stringResource(R.string.total_time_right),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Row(
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(1F),
                    text = rightTankTotalTime,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Composable
fun LapTimes(
    modifier: Modifier = Modifier,
    lapTimes: ImmutableList<String>
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(items = lapTimes) { item ->
            Text(item)
        }
    }
}

@Preview(device = PIXEL_9_PRO)
@Composable
fun PreviewBalancerScreen() {
    TankBalancerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            BalancerScreenContent(
                uiState = BalancerUiState(
                    leftTankTotalTime = "1:20",
                    rightTankTotalTime = "1:30",
                    totalTime = "2:50",
                    currentTank = null,
                    fuelFlow = 12.34,
                    leftTankLapTime = "12.12",
                    leftTankFuel = 23.45,
                    rightTankLapTime = "",
                    rightTankFuel = 12.21,
                    range = "fd",
                    isFlying = false,
                    balancerError = null,
                )
            )
        }
    }
}

