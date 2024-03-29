package io.eflamm.notlelo

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.EventWithDays
import io.eflamm.notlelo.ui.theme.LightGrey
import io.eflamm.notlelo.ui.theme.NotleloTheme
import io.eflamm.notlelo.ui.theme.Red
import io.eflamm.notlelo.viewmodel.*
import io.eflamm.notlelo.views.SelectListStyle
import io.eflamm.notlelo.views.SelectListView
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class Link(val title: String, val isDisabled: Boolean, val route: String, val onClick: (route: String) -> Unit)

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val eventViewModel: EventViewModel by viewModels {
            EventViewModelFactory((application as NotleloApplication).eventRepository)
        }
        val cameraViewModel: CameraViewModel by viewModels {
            CameraViewModelFactory()
        }
        val userPreferencesViewModel: UserPreferencesViewModel by viewModels {
            UserPreferencesViewModelFactory((application as NotleloApplication).userPreferencesRepository)
        }

        val applicationTitle = getString(R.string.lowercase_app_name)

        eventViewModel.clearCache(application.cacheDir)

        setContent {
            NotleloTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NotleloApp(applicationTitle, eventViewModel, cameraViewModel, userPreferencesViewModel)
                }
            }
        }
    }
}

@Composable
fun NotleloApp(
    applicationTitle: String,
    eventViewModel: IEventViewModel,
    cameraViewModel: CameraViewModel,
    userPreferencesViewModel: UserPreferencesViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(route = "camera"){
            CameraScreen(navController, eventViewModel, cameraViewModel, userPreferencesViewModel)
        }
        composable(route = "home"){
            HomeScreen( navController,  applicationTitle,  eventViewModel)
        }
        composable(route = "library"){
            LibraryScreen( navController, eventViewModel)
        }
        composable(route = "settings"){
            SettingsScreen( navController, eventViewModel, userPreferencesViewModel)
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    applicationTitle: String,
    eventViewModel: IEventViewModel
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val (displayAddEvent, setDisplayAddEvent) = remember { mutableStateOf(false) }
    var selectedEvent: EventWithDays? = eventViewModel.uiState.selectedEventWithDays
    val events: List<EventWithDays> = eventViewModel.allEventsWithDays.observeAsState().value ?: emptyList()

    if (events.isNotEmpty()) {
        if (selectedEvent == null) {
            eventViewModel.updateSelectedEventWithDays(events[0])
            selectedEvent = events[0]
        } else {
            val selectedEventId = selectedEvent.event.id
            val updatedSelectedEvent = events.first { event -> event.event.id == selectedEventId }
            if(updatedSelectedEvent == null) {
                // selected event not found
                eventViewModel.updateSelectedEventWithDays(events[0])
                selectedEvent = events[0]
            } else {
                eventViewModel.updateSelectedEventWithDays(updatedSelectedEvent)
                selectedEvent = updatedSelectedEvent
            }
        }
    } else {
        eventViewModel.updateSelectedEventWithDays(null)
    }

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
                fontSize = MaterialTheme.typography.h1.fontSize,
                fontFamily = MaterialTheme.typography.h1.fontFamily,
                color = MaterialTheme.typography.h1.color,
                modifier = Modifier.align(Alignment.Center))
        }
        Box(modifier = Modifier.weight(1.2f),
            contentAlignment = Alignment.BottomCenter) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {

                if(displayAddEvent || events.isEmpty()) {
                    AddEvent(setDisplayAddEvent, events.isEmpty(), eventViewModel)
                } else {
                    SelectEvents(events = events, setDisplayAddEvent, eventViewModel)
                }

                LinkToPage(Link(stringResource(R.string.home_camera), selectedEvent == null, "camera") { route ->
                    when(cameraPermissionState.status) {
                        PermissionStatus.Granted -> {
                            navController.navigate(route)
                        } else -> {
                            // TODO if denied stay on the home page then display toast to explain
                            cameraPermissionState.launchPermissionRequest().also { navController.navigate(route) }
                        }
                    }
                })
                LinkToPage(Link(stringResource(R.string.home_library), selectedEvent == null, "library") {route -> navController.navigate(route)})
                LinkToPage(Link(stringResource(R.string.home_settings), false, "settings") {route -> navController.navigate(route)})
            }
        }
    }
}

@Composable
fun SelectEvents(
    events: List<EventWithDays>,
    setDisplayAddEvent: (Boolean) -> Unit,
    eventViewModel: IEventViewModel
) {
    val (selectedEventName, setSelectedEventName) = remember {
        mutableStateOf(eventViewModel.uiState.selectedEventWithDays?.event?.name ?: events[0].event.name)
    }

    val selectedEventFromUiState: EventWithDays? = eventViewModel.uiState.selectedEventWithDays // TODO remove redundant
    selectedEventFromUiState.let {
        // if an event was selected previously
        // FIXME normally I shouldn't have to check it here, every logic should be handled in the view model
        val hasEventInList = events.any { e -> e.event.name == it?.event!!.name }
        if(hasEventInList) {
            setSelectedEventName(selectedEventFromUiState?.event!!.name)
        }
    }

    Row {
        SelectListView(selectedEventName, events.map { it.event.name },
            onSelect = { index, _ ->
                val newlySelectedEvent = events[index]
                setSelectedEventName(newlySelectedEvent.event.name)
                eventViewModel.updateSelectedEventWithDays(newlySelectedEvent) // FIXME workaround infinite loop
            },
            SelectListStyle(22.sp, 3.sp, FontWeight(400), 50.dp)
        )

        TextButton(
            onClick = {
            setDisplayAddEvent(true)
        }) {
            Icon(
                Icons.Filled.AddCircleOutline,
                contentDescription = stringResource(id = R.string.icon_desc_add_event),
                modifier = Modifier.size(40.dp),
                tint = colorResource(id = R.color.primary)
            )
        }
    }
}

@Composable
fun AddEvent(setDisplayAddEvent: (Boolean) -> Unit, isAddingMandatory: Boolean, eventViewModel: IEventViewModel) {
    val (eventName, setEventName) = remember { mutableStateOf("") }

    Row {
        OutlinedTextField(
            value = eventName,
            onValueChange = { setEventName(it) },
            label = { Text(
                stringResource(id = R.string.home_event_input_placeholder),
                fontSize = 18.sp,
                color = LightGrey
            ) }
        )

        TextButton(onClick = {
            // TODO set the added event as the selected event
            val newEvent = Event(eventName)
            eventViewModel.insertEvent(newEvent).invokeOnCompletion {
                setDisplayAddEvent(false)
            }
        }) {
            Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = stringResource(id = R.string.icon_desc_confirm_add_event),
                modifier = Modifier.size(35.dp),
                tint = colorResource(id = R.color.green)
            )
        }
        TextButton(onClick = {
            setDisplayAddEvent(false)
        }) {
            Icon(
                Icons.Filled.Cancel,
                contentDescription = stringResource(id = R.string.icon_desc_cancel_add_event),
                modifier = Modifier.size(35.dp),
                tint = if(isAddingMandatory) LightGrey else Red
            )
        }
    }
}

@Composable
fun LinkToPage(link: Link) {
    Row {
        TextButton(onClick = {
            if(!link.isDisabled)
                link.onClick(link.route)
       }) {
           Text(
               text = link.title,
               fontFamily = MaterialTheme.typography.h4.fontFamily,
               fontSize = MaterialTheme.typography.h4.fontSize,
               fontWeight = MaterialTheme.typography.h4.fontWeight,
               letterSpacing = MaterialTheme.typography.h4.letterSpacing,
               color = if (link.isDisabled) LightGrey else MaterialTheme.typography.h4.color,
           )
       }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHeader() {
    val eventViewModel: IEventViewModel = MockEventViewModel()
    NotleloTheme {
        HomeScreen(rememberNavController(), "notlelo", eventViewModel)
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({continuation.resume(cameraProvider.get())}, ContextCompat.getMainExecutor(this))
    }
}