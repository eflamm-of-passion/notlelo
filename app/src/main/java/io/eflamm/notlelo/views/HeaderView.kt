package io.eflamm.notlelo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.eflamm.notlelo.R
import io.eflamm.notlelo.ui.theme.NotleloTheme

//class HeaderView : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            NotleloTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//                    Header(this, "Hello world")
//                    Text("hello")
//                }
//            }
//        }
//    }
//}

@Composable
fun HeaderView(navController: NavController, title: String) {
    Row(modifier = Modifier
        .height(80.dp)
        .fillMaxWidth()
        .background(color = colorResource(id = R.color.primary)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button( onClick = { navController.navigateUp() }) {
            Text("Retour")
        }
        Text(title,
            fontSize = 50.sp,
            fontFamily = FontFamily(Font(R.font.caveat_brush, style = FontStyle.Normal)),
            color = colorResource(id = android.R.color.white),
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHeader() {
    NotleloTheme {
        HeaderView(rememberNavController(),  title = "Hello")
    }
}