package com.cedric.tankbalancer.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cedric.tankbalancer.domain.model.FuelUnit
import com.cedric.tankbalancer.presentation.R
import com.cedric.tankbalancer.presentation.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelUnitDropDownMenu(
    modifier: Modifier = Modifier,
    selectedUnit: FuelUnit = FuelUnit.METRIC,
    onChanged: (FuelUnit) -> Unit = {},
) {
    var dropDownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = dropDownExpanded, onExpandedChange = {
            dropDownExpanded = !dropDownExpanded
        }) {
        TextField(
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            value = selectedUnit.asString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = dropDownExpanded,
                    modifier = Modifier.padding(0.dp)
                )
            }
        )
        ExposedDropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false }
        ) {
            FuelUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.asString()) },
                    onClick = {
                        dropDownExpanded = false
                        onChanged.invoke(unit)
                    },
                )
            }
        }
    }
}

@Composable
private fun FuelUnit.asString(): String = when (this) {
    FuelUnit.METRIC -> stringResource(R.string.unit_liter)
    FuelUnit.IMPERIAL -> stringResource(R.string.unit_gallon)
}


@Preview
@Composable
fun PreviewFuelUnitDropDownMenu() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium)
    ) {
        FuelUnitDropDownMenu()
    }
}
