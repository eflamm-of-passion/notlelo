package io.eflamm.notlelo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.eflamm.notlelo.R
import io.eflamm.notlelo.ui.theme.NotleloTheme

@Composable
fun HeaderView(navController: NavController, title: String, childComponent: @Composable () -> Unit) {
    Row(modifier = Modifier
        .height(80.dp)
        .fillMaxWidth()
        .background(color = colorResource(id = R.color.primary)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton( onClick = { navController.navigateUp() }) {
            Icon(
                Icons.Filled.ArrowBackIos,
                contentDescription = stringResource(id = R.string.icon_desc_go_back),
                modifier = Modifier.size(40.dp),
                tint = colorResource(id = R.color.white)
            )
        }
        Text(title,
            fontSize = MaterialTheme.typography.h2.fontSize,
            fontFamily = MaterialTheme.typography.h2.fontFamily,
            color = MaterialTheme.typography.h2.color,
            modifier = Modifier.padding(start = 5.dp)
        )
        childComponent()
    }
}

@Composable
fun HeaderView(navController: NavController, title: String) {
    Row(modifier = Modifier
        .height(80.dp)
        .fillMaxWidth()
        .background(color = colorResource(id = R.color.primary)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton( onClick = { navController.navigateUp() }) {
            Icon(
                Icons.Filled.ArrowBackIos,
                contentDescription = stringResource(id = R.string.icon_desc_go_back),
                modifier = Modifier.size(40.dp),
                tint = colorResource(id = R.color.white)
            )
        }
        Text(title,
            fontSize = MaterialTheme.typography.h2.fontSize,
            fontFamily = MaterialTheme.typography.h2.fontFamily,
            color = MaterialTheme.typography.h2.color,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHeader() {
    NotleloTheme {
        HeaderView(rememberNavController(),  title = "Hello world")
    }
}