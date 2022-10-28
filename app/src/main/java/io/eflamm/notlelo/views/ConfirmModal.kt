package io.eflamm.notlelo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.eflamm.notlelo.R
import io.eflamm.notlelo.ui.theme.Green
import io.eflamm.notlelo.ui.theme.Red
import io.eflamm.notlelo.ui.theme.White

@Composable
fun ConfirmModal(title: String, confirmAction: ()  -> Unit, cancelAction: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black.copy(alpha = 0.6f)   ) {
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Card(modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 100.dp, start = 20.dp, end = 20.dp)
                .heightIn(min = 150.dp)
                .clip(RoundedCornerShape(7.dp)),
                shape = RoundedCornerShape(7.dp),
            ) {
                Column(
                    modifier = Modifier.background(color = White).padding(5.dp).wrapContentSize(Alignment.Center),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = title, textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.h6.fontSize, color = MaterialTheme.typography.h6.color,
                        modifier = Modifier.padding(5.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column {
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = Red),
                                onClick = { cancelAction() }
                            ) {
                                Text(text = stringResource(id = R.string.camera_cancel), fontSize = MaterialTheme.typography.button.fontSize, color = MaterialTheme.typography.button.color)
                            }
                        }
                        Column {
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = Green),
                                onClick = { confirmAction() }
                            ) {
                                Text(text = stringResource(id = R.string.camera_validate), fontSize = MaterialTheme.typography.button.fontSize, color = MaterialTheme.typography.button.color)
                            }
                        }
                    }
                }
            }
        }
    }
}