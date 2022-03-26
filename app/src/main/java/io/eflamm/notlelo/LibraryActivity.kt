package io.eflamm.notlelo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.eflamm.notlelo.ui.theme.NotleloTheme
import io.eflamm.notlelo.views.HeaderView

class LibraryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            NotleloTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//                    LibraryView()
//                }
//            }
//        }
    }
}

@Composable
fun LibraryView(navController: NavController){
    Column(modifier = Modifier
        .height(IntrinsicSize.Max)
        .width(IntrinsicSize.Max)
        .background(color = colorResource(id = R.color.white))) {
        HeaderView(navController, stringResource(id = R.string.home_library))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotleloTheme {
        LibraryView(rememberNavController())
    }
}