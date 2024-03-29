package io.eflamm.notlelo.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import io.eflamm.notlelo.R
import io.eflamm.notlelo.ui.theme.DarkGrey
import io.eflamm.notlelo.ui.theme.LightGrey
import io.eflamm.notlelo.ui.theme.LighterGrey
import io.eflamm.notlelo.ui.theme.White

data class SelectListStyle(
        val fontSize: TextUnit,
        val letterSpacing: TextUnit,
        val fontWeight: FontWeight,
        val iconSize: Dp
    )

@Composable
fun SelectListView(value: String, items: List<String>, onSelect: (index: Int, item: String) -> Unit, selectListStyle: SelectListStyle = SelectListStyle(16.sp, 0.sp, FontWeight.Normal, 25.dp)  ) {
    val (textFieldSize, setTextFieldSize) = remember { mutableStateOf(Size.Zero) }
    val (isExpanded, setExpanded) = remember { mutableStateOf(false) }

    val icon = if (isExpanded)
        Icons.Filled.ExpandLess
    else
        Icons.Filled.ExpandMore

    Row(modifier = Modifier
        .width(300.dp)
        .onGloballyPositioned { coordinates ->
            //This value is used to assign to the DropDown the same width
            setTextFieldSize(coordinates.size.toSize())
        }
    ) {
        TextButton(
            onClick = { setExpanded(!isExpanded) },

        ) {
            Column(
                Modifier
                    .width(250.dp)
                    .height(50.dp)
                    .drawBehind {
                        val strokeWidth = 5f
                        val x = size.width - strokeWidth
                        val y = size.height - strokeWidth

                        //top line
                        drawLine(
                            color = if(isExpanded) LightGrey else LighterGrey,
                            start = Offset(0f, y),// bottom-left point of the box
                            end = Offset(x, y),// bottom-right point of the box
                            strokeWidth = strokeWidth
                        )
                    },
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    fontFamily = MaterialTheme.typography.body1.fontFamily,
                    fontSize = selectListStyle.fontSize,
                    fontWeight = selectListStyle.fontWeight,
                    letterSpacing = selectListStyle.letterSpacing,
                    color = MaterialTheme.typography.body1.color,
                    modifier = Modifier
                        .padding(top = 5.dp, start = 15.dp, bottom = 5.dp, end = 5.dp),
                )
            }
            Column {
                Icon(
                    icon,
                    modifier = Modifier.size(selectListStyle.iconSize),
                    contentDescription = if (isExpanded) stringResource(id = R.string.icon_desc_selecting_value) else stringResource(id = R.string.icon_desc_selected_value),
                    tint = DarkGrey
                )
            }
        }
    }

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
                Text(text = item, color = White)
            }
        }
    }
}