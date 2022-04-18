package io.eflamm.notlelo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.eflamm.notlelo.databinding.HomeActivityBinding
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.Link
import io.eflamm.notlelo.ui.theme.NotleloTheme
import io.eflamm.notlelo.viewmodel.IEventViewModel
import io.eflamm.notlelo.viewmodel.MockEventViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeActivityBinding
//    private var events: List<Event> = emptyList()
    private lateinit var selectedEvent: Event
//    private val eventViewModel: EventViewModel by viewModels {
//        EventViewModelFactory((application as NotleloApplication).eventRepository)
//    }
    private val eventViewModel: MockEventViewModel = MockEventViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        /** TODO check if there are at least one camp, otherwise
            display add event layout
            grey the buttons
         **/
        super.onCreate(savedInstanceState)

//        setContentView(binding.root)

        val applicationTitle = getString(R.string.lowercase_app_name)
        var events: List<Event> = emptyList()
        eventViewModel.allEvents.observe(this) { events = it }
        val links: List<Link> = listOf(
            Link(getString(R.string.home_camera), "camera"),
            Link(getString(R.string.home_library), "library"),
            Link(getString(R.string.home_settings), "settings")
        )

        setContent {
            NotleloTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NotleloApp(applicationTitle, events, links, eventViewModel)
                }
            }
        }
    }
}

@Composable
fun NotleloApp(
    applicationTitle: String,
    events: List<Event>,
    links: List<Link>,
    eventViewModel: IEventViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home"){
            HomeView(navController = navController, applicationTitle = applicationTitle, events = events, links = links, eventViewModel)
        }
        composable(route = "library"){
            LibraryView(navController = navController)
        }
        composable(route = "settings"){
            SettingsView(navController = navController)
        }
    }
}


@Composable
fun HomeView(
    navController: NavController,
    applicationTitle: String,
    events: List<Event>,
    links: List<Link>,
    eventViewModel: IEventViewModel
) {
    val (displayAddEvent, setDisplayAddEvent) = remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(0.8f), contentAlignment = Alignment.TopCenter) {
            Image(painter = painterResource(id = R.drawable.ripped_paper),
                contentDescription = "Background of the home page",
                modifier = Modifier
                    .scale(scaleX = 1.3f, scaleY = 1.3f)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop)
            Text(text = applicationTitle,
                fontSize = 120.sp,
                fontFamily = FontFamily(Font(R.font.caveat_brush, style = FontStyle.Normal)),
                color = colorResource(id = android.R.color.white),
                modifier = Modifier.align(Alignment.Center))
        }
        Box(modifier = Modifier.weight(1.2f),
            contentAlignment = Alignment.BottomCenter) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                if(displayAddEvent) {
                    AddEvent(setDisplayAddEvent, eventViewModel)
                } else {
                    SelectEvents(events = events, setDisplayAddEvent)
                }
                    for(link in links) {
                        LinkToPage(navController, link)
                    }
            }
        }
    }
}

@Composable
fun SelectEvents(events: List<Event>, setDisplayAddEvent: (Boolean) -> Unit) {
    // TODO display the text input when the list is empty
    val (isExpanded, setExpanded) = remember { mutableStateOf(false) }
    val (selectedEvent, setSelectedEvent) = remember { mutableStateOf("hello") }
    val l = listOf<String>("hello", "world")
    val (textfieldSize, setTextfieldSize) = remember { mutableStateOf(Size.Zero) }

    Row() {
        val icon = if (isExpanded)
            Icons.Filled.ArrowDropUp
        else
            Icons.Filled.ArrowDropDown

        OutlinedTextField(
            value = selectedEvent,
            onValueChange = { setSelectedEvent(it) },
            modifier = Modifier
                .width(300.dp)
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    setTextfieldSize(coordinates.size.toSize())
                },
            label = {Text("Label")},
            trailingIcon = {
                Icon(icon,"contentDescription", Modifier.clickable { setExpanded(false) }, tint = colorResource(id = android.R.color.darker_gray))
            },
            colors = TextFieldDefaults.textFieldColors( textColor = colorResource(id = android.R.color.darker_gray))
        )

        // FIXME the dropdown menu
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { setExpanded(false) },
            modifier = Modifier
                .width(with(LocalDensity.current){textfieldSize.width.toDp()})
        ) {
            l.forEach { label ->
                DropdownMenuItem(onClick = {
                    setSelectedEvent(label)
                }) {
                    Text(text = label, color = colorResource(id = android.R.color.black))
                }
            }
        }
//        Button(onClick = {
//            setDisplayAddEvent(true)
//        }) {
//            Text(text = "add")
//        }
    }
    Row() {
        l.forEach { s ->
            Text(text = s, color = colorResource(id = android.R.color.black))
        }
    }
}

@Composable
fun AddEvent(setDisplayAddEvent: (Boolean) -> Unit, eventViewModel: IEventViewModel) {
    val (eventName, setEventName) = remember { mutableStateOf("") }

    Row {
        TextField(value = eventName,
            colors = TextFieldDefaults.textFieldColors( textColor = colorResource(id = android.R.color.darker_gray)),
            placeholder = { Text(stringResource(id = R.string.home_event_input_placeholder)) },
            onValueChange = { setEventName(it)})
        Button(onClick = {
            val newEvent = Event(eventName)
            eventViewModel.insert(newEvent)
            setDisplayAddEvent(false)
        }) {
            Text(text = "add")
        }
        Button(onClick = {
            setDisplayAddEvent(false)
        }) {
            Text(text = "cancel")
        }
    }
}

@Composable
fun LinkToPage(navigateController: NavController, link: Link) {
    Row() {
       Button(onClick = {
           navigateController.navigate(link.route)
       }) {
           Text(text = link.title)
       }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHeader() {
    val event1 = Event(name = "Camp bleu")
    val event2 = Event(name = "Camp rouge")
    val events = listOf(event1, event2)
    val links: List<Link> = listOf(
        Link(stringResource(id = R.string.home_camera), "camera"),
        Link(stringResource(id = R.string.home_library), "library"),
        Link(stringResource(id = R.string.home_settings), "settings")
    )
    val eventViewModel: IEventViewModel = MockEventViewModel()
    NotleloTheme {
        HomeView(rememberNavController(), "notlelo", events = events, links= links, eventViewModel)
    }
}