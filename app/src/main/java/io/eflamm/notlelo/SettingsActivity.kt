package io.eflamm.notlelo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.eflamm.notlelo.databinding.SettingsActivityBinding
import io.eflamm.notlelo.ui.theme.NotleloTheme
import io.eflamm.notlelo.viewmodel.EventViewModel
import io.eflamm.notlelo.viewmodel.EventViewModelFactory
import io.eflamm.notlelo.views.HeaderView

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding
    private lateinit var eventsToDelete: MutableList<String>

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as NotleloApplication).eventRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)

        eventsToDelete = mutableListOf()

    }
}


@Composable
fun SettingsView(navController: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))) {
        HeaderView(navController, stringResource(id = R.string.home_settings))
        Box(modifier = Modifier.weight(1f)) {
            Column {
                SectionTitle(stringResource(id = R.string.settings_deleteEvent))
                Button(onClick = {}) {
                    Text(text = stringResource(id = R.string.settings_deleteEvent))
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            Column {
                SectionTitle(stringResource(id = R.string.faq_title))
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            Column {
                SectionTitle(stringResource(id = R.string.settings_about))
                Text(text = stringResource(id = R.string.settings_aboutDescription), color = colorResource(
                    id = R.color.secondary
                ))
                Button(onClick = {}) {
                    Text(text = stringResource(id = R.string.settings_clickHere))
                }
                Text(text = stringResource(id = R.string.settings_signature), color = colorResource(
                    id = R.color.secondary
                ))
                Text(text = "version", color = colorResource(
                    id = R.color.secondary
                ))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, fontSize = 25.sp, color = colorResource(id = R.color.secondary), modifier = Modifier.padding(start = 10.dp))
}

@Preview(showBackground = true)
@Composable
fun previewActivity() {
    NotleloTheme {
        SettingsView(rememberNavController())
    }
}