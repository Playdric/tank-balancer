package com.cedric.tankbalancer.presentation.screen.balancer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_9_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cedric.domain.model.AircraftTank
import com.cedric.tankbalancer.presentation.R
import com.cedric.tankbalancer.presentation.composable.AirplaneView
import com.cedric.tankbalancer.presentation.composable.ErrorDialog
import com.cedric.tankbalancer.presentation.composable.FuelGauge
import com.cedric.tankbalancer.presentation.composable.RepeatIconButton
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import com.cedric.tankbalancer.presentation.theme.TankBalancerTheme
import com.cedric.tankbalancer.presentation.theme.spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

private const val TAG = "BalancerScreen"

@Composable
fun BalancerScreen(
    arguments: TankBalancerNavEntry.BalancerScreen.Arguments? = null,
    navigate: (TankBalancerNavEntry) -> Unit = {},
) {
    val viewModel: BalancerViewModel = koinViewModel(parameters = { parametersOf(arguments) })

    val balancerUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigationEvent by viewModel.navigationEvent.collectAsStateWithLifecycle(null)

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { navigate.invoke(it) }
    }

    BalancerScreenContent(uiState = balancerUiState, action = viewModel::onAction, arguments = arguments)

}

@Composable
fun BalancerScreenContent(
    uiState: BalancerUiState,
    action: (BalancerAction) -> Unit = {},
    arguments: TankBalancerNavEntry.BalancerScreen.Arguments? = null
) {
    var showLandingDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (uiState.balancerError != null || showLandingDialog) Modifier.blur(30.dp) else Modifier)
    ) {
        TotalTimes(
            leftTankTotalTime = uiState.leftTankTotalTime,
            totalTime = uiState.totalTime,
            rightTankTotalTime = uiState.rightTankTotalTime,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AirplaneView(
                        modifier = Modifier
                            .size(200.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FuelGauge(modifier = Modifier.width(80.dp), isSelected = false)
                    FuelGauge(modifier = Modifier.width(80.dp), textColor = MaterialTheme.colorScheme.background)
                }
            }

        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        when (uiState.flightStatus) {
            FlightStatus.BEFORE_TAKE_OFF -> {
                BeforeTakeOffControls(onClickTakeOff = {
                    arguments?.let { args ->
                        action.invoke(
                            BalancerAction.TakeOff(
                                initialFuelLeft = args.initialFuelRight,
                                initialFuelRight = args.initialFuelRight,
                                initialFuelFlow = args.initialFuelFlow,
                                initialTank = args.initialTank
                            )
                        )
                    } ?: run {
                        Timber.tag(TAG).e("Take Off clicked but arguments are null")
                    }

                })
            }

            FlightStatus.FLYING -> {
                FlyingControls(
                    onRepeatDecrease = { action(BalancerAction.DecreaseFuelFlow) },
                    onReleaseDecrease = { action(BalancerAction.ValidFuelFlow) },
                    onRepeatIncrease = { action(BalancerAction.IncreaseFuelFlow) },
                    onReleaseIncrease = { action(BalancerAction.ValidFuelFlow) },
                    onClickSwitchTank = { action(BalancerAction.SwitchTank) },
                    fuelFlow = uiState.fuelFlow,
                )
            }

            FlightStatus.STOPOVER -> {}
            FlightStatus.LANDED -> {
                Landed()
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        LapTimes(
            lapTimes = uiState.lapTimes
        )
        if (uiState.flightStatus == FlightStatus.FLYING) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.medium), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    showLandingDialog = true
                }) {
                    Text(stringResource(R.string.land))
                }
            }
        }
    }

    if (showLandingDialog) {
        ErrorDialog(
            onDismissRequest = { showLandingDialog = false },
            onConfirmation = {
                action(BalancerAction.ConfirmLanding)
                showLandingDialog = false
            },
            dialogTitle = stringResource(R.string.warning),
            dialogText = stringResource(R.string.warning_landing),
        )
    }
}

@Composable
fun Landed() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.medium), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(MaterialTheme.spacing.large),
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(R.string.flight_ended)
        )
    }
}

@Composable
private fun FlyingControls(
    onRepeatDecrease: () -> Unit,
    onReleaseDecrease: () -> Unit,
    onRepeatIncrease: () -> Unit,
    onReleaseIncrease: () -> Unit,
    onClickSwitchTank: () -> Unit,
    fuelFlow: Double,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.medium), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RepeatIconButton(
            onRepeat = onRepeatDecrease,
            onRelease = onReleaseDecrease,
            icon = Icons.Default.Remove,
            enabled = fuelFlow > 0
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
        val color = MaterialTheme.colorScheme.surfaceContainer
        Text(
            modifier = Modifier
                .drawBehind {
                    drawRoundRect(
                        color = color,
                        cornerRadius = CornerRadius(20F, 20F)
                    )
                }
                .padding(start = MaterialTheme.spacing.medium, end = MaterialTheme.spacing.medium),
            text = fuelFlow.toString(),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

        RepeatIconButton(
            onRepeat = onRepeatIncrease,
            onRelease = onReleaseIncrease,
            icon = Icons.Default.Add,
        )
    }
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.medium), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onClickSwitchTank) {
            Text(stringResource(R.string.switch_tank))
        }
    }
}

@Composable
fun BeforeTakeOffControls(onClickTakeOff: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = onClickTakeOff) {
            Text(stringResource(R.string.take_off))
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
fun ColumnScope.LapTimes(
    modifier: Modifier = Modifier,
    lapTimes: ImmutableList<UiLapTime>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
            .padding(horizontal = MaterialTheme.spacing.medium)
            .clip(RoundedCornerShape(MaterialTheme.spacing.medium)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            items(items = lapTimes) { item ->
                LapTimeItem(
                    tank = item.tank,
                    startTime = item.startTime,
                )
            }
        }
    }
}

@Composable
fun LapTimeItem(
    tank: AircraftTank,
    startTime: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.spacing.medium))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(MaterialTheme.spacing.medium),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = startTime,
            textAlign = if (tank == AircraftTank.LEFT) TextAlign.Left else TextAlign.Right
        )
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
                    flightStatus = FlightStatus.FLYING,
                    balancerError = null,
                    lapTimes = persistentListOf(
                        UiLapTime(
                            tank = AircraftTank.LEFT,
                            startTime = "00:01"
                        ),
                        UiLapTime(
                            tank = AircraftTank.RIGHT,
                            startTime = "00:01"
                        ),
//                        UiLapTime(
//                            tank = AircraftTank.LEFT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.RIGHT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.LEFT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.RIGHT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.LEFT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.RIGHT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.LEFT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.RIGHT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.LEFT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.RIGHT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.LEFT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.RIGHT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.LEFT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.RIGHT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.LEFT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.RIGHT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.LEFT,
//                            startTime = "00:01"
//                        ),
//                        UiLapTime(
//                            tank = AircraftTank.RIGHT,
//                            startTime = "00:01"
//                        ),
                    )
                )
            )
        }
    }
}

