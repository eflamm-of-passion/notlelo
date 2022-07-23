package io.eflamm.notlelo.views

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

@Composable
fun SelectListView(value: String, items: List<String>, onSelect: (index: Int, item: String) -> Unit, onChange: (changedValue: String) -> Unit) {
    val (textFieldSize, setTextFieldSize) = remember { mutableStateOf(Size.Zero) }
    val (isExpanded, setExpanded) = remember { mutableStateOf(false) }

    val icon = if (isExpanded)
        Icons.Filled.ArrowDropUp
    else
        Icons.Filled.ArrowDropDown

    OutlinedTextField(
        value = value,
        onValueChange = {   changedValue ->
            // maybe do something here
            // but I should just change the type of input
            onChange(changedValue)
        },
        modifier = Modifier
            .width(300.dp)
            .onGloballyPositioned { coordinates ->
                //This value is used to assign to the DropDown the same width
                setTextFieldSize(coordinates.size.toSize())
            },
        label = { Text("Label") },
        trailingIcon = {
            Icon(icon,"contentDescription", Modifier.clickable { setExpanded(!isExpanded) }, tint = colorResource(id = R.color.darker_gray))
        },
        colors = TextFieldDefaults.textFieldColors( textColor = colorResource(id = R.color.darker_gray))
    )

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { setExpanded(false) },
        modifier = Modifier.width(with(LocalDensity.current){textFieldSize.width.toDp()})
    ) {
        items.forEachIndexed { index, item ->
            DropdownMenuItem(onClick = {
                onSelect(index, item)
                setExpanded(false)
            }) {
                Text(text = item, color = colorResource(id = android.R.color.white))
            }
        }
    }
}