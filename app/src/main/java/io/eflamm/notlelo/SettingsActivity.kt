package io.eflamm.notlelo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.ui.theme.NotleloTheme
import io.eflamm.notlelo.viewmodel.IEventViewModel
import io.eflamm.notlelo.viewmodel.MockEventViewModel
import io.eflamm.notlelo.views.HeaderView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}


@Composable
fun SettingsView(navController: NavController, eventViewModel: IEventViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))) {
        HeaderView(navController, stringResource(id = R.string.home_settings))
        Box(modifier = Modifier.weight(1f)) {
            DeleteEvent(eventViewModel)
        }
        Box(modifier = Modifier.weight(1f)) {
            FrequentlyAskedQuestions()
        }
        Box(modifier = Modifier.weight(1f)) {
            About()
        }
    }
}

@Composable
fun DeleteEvent(eventViewModel: IEventViewModel) {
    val eventsToDelete = remember { mutableStateListOf<Event>() }
    val events: List<Event>? = eventViewModel.allEvents.observeAsState().value

    Column {
        SectionTitle(stringResource(id = R.string.settings_deleteEvent))
        Button(onClick = {
            eventViewModel.deleteEvents(eventsToDelete.toList())
            // TODO add a toast to confirm the deletion
        }) {
            Text(text = stringResource(id = R.string.settings_deleteEvent))
        }
        events?.forEach { event ->
            Row(modifier = Modifier.height(40.dp)) {
                Checkbox(
                    colors = CheckboxDefaults.colors(Color.LightGray),
                    checked = eventsToDelete.contains(event),
                    onCheckedChange = {checked ->
                        if (checked) {
                            if (!eventsToDelete.contains(event)) {
                                 eventsToDelete.add(event)
                            }
                        } else {
                            if(eventsToDelete.contains(event)) {
                                eventsToDelete.remove(event)
                            }
                        }
                    })
                    Text(
                        text = event.name,
                        color = colorResource(id = R.color.secondary),
                        modifier = Modifier.wrapContentHeight()
                    )
            }
        }
    }
}

@Composable
fun FrequentlyAskedQuestions() {
    Column {
        SectionTitle(stringResource(id = R.string.faq_title))
    }
}

@Composable
fun About() {
    Column {
        SectionTitle(stringResource(id = R.string.settings_about))
        Text(text = stringResource(id = R.string.settings_aboutDescription),
            fontSize = MaterialTheme.typography.body1.fontSize,
            fontWeight = MaterialTheme.typography.body1.fontWeight,
            letterSpacing = MaterialTheme.typography.body1.letterSpacing,
            color = MaterialTheme.typography.body1.color
        )
        Button(onClick = {}) {
            Text(text = stringResource(id = R.string.settings_clickHere),
                fontSize = MaterialTheme.typography.button.fontSize,
                fontWeight = MaterialTheme.typography.button.fontWeight,
                letterSpacing = MaterialTheme.typography.button.letterSpacing,
                color = MaterialTheme.typography.button.color
            )
        }
        Text(text = stringResource(id = R.string.settings_signature),
            fontSize = MaterialTheme.typography.body1.fontSize,
            fontWeight = MaterialTheme.typography.body1.fontWeight,
            letterSpacing = MaterialTheme.typography.body1.letterSpacing,
            color = MaterialTheme.typography.body1.color
        )
        Text(text = "version",
            fontSize = MaterialTheme.typography.body1.fontSize,
            fontWeight = MaterialTheme.typography.body1.fontWeight,
            letterSpacing = MaterialTheme.typography.body1.letterSpacing,
            color = MaterialTheme.typography.body1.color
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = MaterialTheme.typography.h3.fontSize,
        color = MaterialTheme.typography.h3.color,
        modifier = Modifier.padding(start = 10.dp))
}

@Preview(showBackground = true)
@Composable
fun previewActivity() {
    val eventViewModel: IEventViewModel = MockEventViewModel()
    NotleloTheme {
        SettingsView(rememberNavController(), eventViewModel)
    }
}