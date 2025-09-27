@file:OptIn(ExperimentalMaterial3Api::class)

package com.cedric.tankbalancer.presentation.screen.setup

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices.PIXEL
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.cedric.domain.model.AircraftTank
import com.cedric.tankbalancer.presentation.R
import com.cedric.tankbalancer.presentation.composable.FuelUnitDropDownMenu
import com.cedric.tankbalancer.presentation.navigation.TankBalancerNavEntry
import com.cedric.tankbalancer.presentation.theme.TankBalancerTheme
import com.cedric.tankbalancer.presentation.theme.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun SetupScreen(
    viewModel: SetupViewModel = koinViewModel(),
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


    SetupScreenContent(setupUiState = uiState, action = viewModel::onAction)

}

@Composable
fun SetupScreenContent(setupUiState: SetupUiState, action: (SetupAction) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium)
    ) {

        FuelQuantitySettingRow(
            text = stringResource(R.string.left_wing_fuel),
            initialValue = setupUiState.leftFuel.toString(),
            onValueChanged = { action.invoke(SetupAction.ChangedLeftFuel(it)) },
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        FuelQuantitySettingRow(
            text = stringResource(R.string.right_wing_fuel),
            initialValue = setupUiState.rightFuel.toString(),
            onValueChanged = { action.invoke(SetupAction.ChangedRightFuel(it)) },
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        FuelQuantitySettingRow(
            text = stringResource(R.string.estimated_fuel_flow),
            initialValue = setupUiState.fuelFlow.toString(),
            onValueChanged = { action.invoke(SetupAction.ChangedFuelFlow(it)) },
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                modifier = Modifier.weight(.7F),
                text = stringResource(R.string.fuel_unit)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.large))
            FuelUnitDropDownMenu(
                modifier = Modifier.weight(.3F),
                selectedUnit = setupUiState.fuelUnit,
                onChanged = { action.invoke(SetupAction.ChangedFuelUnit(it)) }
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val options = AircraftTank.entries
            Text(
                modifier = Modifier.weight(.7F),
                text = stringResource(R.string.starting_tank)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.large))
            SingleChoiceSegmentedButtonRow {
                options.forEach { tank ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = tank.index,
                            count = options.size
                        ),
                        onClick = {
                            action(SetupAction.ChangedStartingTank(newStartingTank = tank.index))
                        },
                        selected = tank.index == setupUiState.startingTank.index,
                        label = { Text(text = stringResource(tank.asStringResource())) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xxLarge))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                action.invoke(SetupAction.ConfirmSetup)
            }) {
                Text(stringResource(R.string.validate))
            }
        }

    }
}

private fun AircraftTank.asStringResource(): Int = when (this) {
    AircraftTank.LEFT -> R.string.left
    AircraftTank.RIGHT -> R.string.right
}


@Composable
private fun FuelQuantitySettingRow(
    text: String,
    initialValue: String,
    onValueChanged: (String) -> Unit,
) {
    var fuelValue by remember { mutableStateOf(TextFieldValue()) }
    val focusManager = LocalFocusManager.current
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            modifier = Modifier.weight(.7F),
            text = text
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
        TextField(
            modifier = Modifier
                .weight(.3F),
            placeholder = { Text(text = initialValue) },
            value = fuelValue,
            singleLine = true,
            onValueChange = {
                fuelValue = it
                onValueChanged.invoke(fuelValue.text)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = {
                onValueChanged.invoke(fuelValue.text)
                focusManager.clearFocus()
            })
        )
    }
}

@Preview(device = PIXEL)
@Composable
fun PreviewSetupScreenContentPreview() {
    TankBalancerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            SetupScreenContent(setupUiState = SetupUiState(), action = {})
        }
    }
}
