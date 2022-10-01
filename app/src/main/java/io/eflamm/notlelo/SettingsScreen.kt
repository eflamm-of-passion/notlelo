package io.eflamm.notlelo

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.ui.theme.LightGrey
import io.eflamm.notlelo.ui.theme.NotleloTheme
import io.eflamm.notlelo.ui.theme.Red
import io.eflamm.notlelo.ui.theme.White
import io.eflamm.notlelo.viewmodel.IEventViewModel
import io.eflamm.notlelo.viewmodel.IUserPreferencesViewModel
import io.eflamm.notlelo.viewmodel.MockEventViewModel
import io.eflamm.notlelo.viewmodel.MockUserPreferencesViewModel
import io.eflamm.notlelo.views.HeaderView
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(navController: NavController, eventViewModel: IEventViewModel, userPreferencesViewModel: IUserPreferencesViewModel) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
    ) {
        item {
            HeaderView(navController, stringResource(id = R.string.home_settings))
            DeleteEvent(eventViewModel)
            CameraSettings(userPreferencesViewModel)
            FrequentlyAskedQuestions()
            About()
        }
    }
}

@Composable
fun DeleteEvent(eventViewModel: IEventViewModel) {
    val eventsToDelete = remember { mutableStateListOf<Event>() }
    val events: List<Event>? = eventViewModel.allEvents.observeAsState().value

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        SectionTitle(stringResource(id = R.string.settings_deleteEvent))
        Column(modifier = Modifier.fillMaxWidth()) {
            if(events?.isEmpty() == true) {
                Text(
                    text = stringResource(id = R.string.settings_noCamp),
                    fontSize = 5.em,
                    color = LightGrey,
                    modifier = Modifier.wrapContentHeight(),
                    style = TextStyle(fontStyle = FontStyle.Italic)
                )
            }
            events?.forEach { event ->
                Row {
                    Checkbox(
                        modifier = Modifier.padding(start = 100.dp, end = 20.dp),
                        colors =CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colors.primary,
                            uncheckedColor = MaterialTheme.colors.primary,
                            checkmarkColor = White
                        ),
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
                        Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                            Text(
                                text = event.name,
                                fontSize = 22.sp,
                                fontFamily = MaterialTheme.typography.body1.fontFamily,
                                fontWeight = MaterialTheme.typography.body1.fontWeight,
                                letterSpacing = MaterialTheme.typography.body1.letterSpacing,
                                color = colorResource(id = R.color.secondary)
                            )
                        }
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                eventViewModel.deleteEvents(eventsToDelete.toList())
                // TODO add a toast to confirm the deletion
                },
                colors = if (eventsToDelete.isEmpty())  ButtonDefaults.buttonColors(backgroundColor = LightGrey) else ButtonDefaults.buttonColors(backgroundColor = Red),
            ) {
                Text(text = stringResource(id = R.string.settings_deleteEvent),
                    fontSize = 5.em,
                    fontFamily = MaterialTheme.typography.button.fontFamily,
                    fontWeight = FontWeight.W300,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.typography.button.color
                )
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = stringResource(id = R.string.settings_deleteEvent),
                    modifier = Modifier.size(30.dp),
                    tint = White
                )
            }
        }
    }
}

@Composable
fun CameraSettings(userPreferencesViewModel: IUserPreferencesViewModel) {
    var resolutionFromUserPreference: Int? = userPreferencesViewModel.pictureResolution.observeAsState().value
    var sliderPosition by remember { mutableStateOf(0f)}
    SectionTitle( stringResource(id = R.string.settings_picture_resolution))
    Text(text = stringResource(id = R.string.settings_picture_resolution_description),
        fontSize = MaterialTheme.typography.body1.fontSize,
        fontFamily = MaterialTheme.typography.body1.fontFamily,
        fontWeight = MaterialTheme.typography.body1.fontWeight,
        letterSpacing = MaterialTheme.typography.body1.letterSpacing,
        color = MaterialTheme.typography.body1.color
    )
    Text(text = stringResource(id = R.string.settings_picture_resolution_value, "$resolutionFromUserPreference"),
        fontSize = MaterialTheme.typography.body1.fontSize,
        fontFamily = MaterialTheme.typography.body1.fontFamily,
        fontWeight = MaterialTheme.typography.body1.fontWeight,
        letterSpacing = MaterialTheme.typography.body1.letterSpacing,
        color = MaterialTheme.typography.body1.color
    )
    if(resolutionFromUserPreference != null) {
        Row(modifier = Modifier.padding(start = 50.dp, end = 50.dp, top = 25.dp)) {
            Slider(
                value = mapResolutionToSliderPosition(resolutionFromUserPreference),
                valueRange = 0f..2f,
                steps = 1,
                onValueChange = { position ->
                    sliderPosition = position
                },
                onValueChangeFinished = {
                    userPreferencesViewModel.updatePictureResolution(mapSliderPositionToResolution(sliderPosition))
                }
            )
        }
    }
}

private fun mapResolutionToSliderPosition(pictureResolution: Int): Float {
    return when(pictureResolution) {
        240 -> 0f
        480 -> 1f
        720 -> 2f
        else -> 0f
    }
}

private fun mapSliderPositionToResolution(sliderPosition: Float): Int {
    return when(sliderPosition.roundToInt().toFloat()) {
        0f -> 240
        1f -> 480
        2f -> 720
        else -> 240
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FrequentlyAskedQuestions() {
    Column {
        SectionTitle(stringResource(id = R.string.faq_title))
        ExpandableSection(title = stringResource(id = R.string.faq_archive_event_question)) {
            AnswerArchiveEvent()
        }
        ExpandableSection(title = stringResource(id = R.string.faq_availableOnIOS_question)) {
            AnswerText(stringResource(id = R.string.faq_availableOnIOS_answer))
        }
        ExpandableSection(title = stringResource(id = R.string.faq_found_a_bug_question)) {
            AnswerText(stringResource(id = R.string.faq_found_a_bug_answer))
        }
    }
}

@Composable
fun AnswerArchiveEvent() {
    Text(text = buildAnnotatedString {
        append(stringResource(id = R.string.faq_archive_event_answer1))
        appendInlineContent("shareIcon", stringResource(id = R.string.icon_desc_share_event))
        append(stringResource(id = R.string.faq_archive_event_answer2))
    }, inlineContent = mapOf(
        Pair("shareIcon", InlineTextContent(
            Placeholder(width = 2.em, height = 18.sp, placeholderVerticalAlign = PlaceholderVerticalAlign.TextTop)
        ) {
            Icon(
                Icons.Filled.Share,
                contentDescription = stringResource(id = R.string.icon_desc_share_event),
                modifier = Modifier
                    .size(50.dp)
                    .background(White),
                tint = colorResource(id = R.color.primary)
            )
        }
        )),
        color = MaterialTheme.typography.body1.color,
        fontFamily = MaterialTheme.typography.body1.fontFamily,
        fontSize = MaterialTheme.typography.body1.fontSize,
        modifier = Modifier.background(White).fillMaxWidth()
    )
}

@Composable
fun AnswerText(answer: String) {
    Text(text = answer,
        fontSize = MaterialTheme.typography.body1.fontSize,
        fontFamily = MaterialTheme.typography.body1.fontFamily,
        fontWeight = MaterialTheme.typography.body1.fontWeight,
        letterSpacing = MaterialTheme.typography.body1.letterSpacing,
        color = MaterialTheme.typography.body1.color,
        modifier = Modifier.background(White).fillMaxWidth()
    )
}

@Composable
fun About() {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionTitle(stringResource(id = R.string.settings_about))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text = stringResource(id = R.string.settings_aboutDescription),
                fontSize = MaterialTheme.typography.body1.fontSize,
                fontFamily = MaterialTheme.typography.body1.fontFamily,
                fontWeight = MaterialTheme.typography.body1.fontWeight,
                letterSpacing = MaterialTheme.typography.body1.letterSpacing,
                color = MaterialTheme.typography.body1.color,
                modifier = Modifier.background(White).padding(bottom = 20.dp)
            )
            Button(onClick = {
                    val intent = sendMailToMe()
                    if(intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .height(50.dp)
                    .width(250.dp)
            ) {
                Text(text = stringResource(id = R.string.settings_clickHere),
                    fontSize = 5.em,
                    fontFamily = MaterialTheme.typography.button.fontFamily,
                    fontWeight = FontWeight.W300,
                    letterSpacing = 3.sp,
                    color = MaterialTheme.typography.button.color
                )
            }
            Text(text = stringResource(id = R.string.settings_signature),
                fontSize = MaterialTheme.typography.body1.fontSize,
                fontFamily = MaterialTheme.typography.body1.fontFamily,
                fontWeight = MaterialTheme.typography.body1.fontWeight,
                letterSpacing = MaterialTheme.typography.body1.letterSpacing,
                color = MaterialTheme.typography.body1.color,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(text = "v${BuildConfig.VERSION_NAME}",
                fontSize = MaterialTheme.typography.body1.fontSize,
                fontFamily = MaterialTheme.typography.body1.fontFamily,
                fontWeight = MaterialTheme.typography.body1.fontWeight,
                letterSpacing = MaterialTheme.typography.body1.letterSpacing,
                color = MaterialTheme.typography.body1.color,
                modifier = Modifier.padding(top = 15.dp)
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = MaterialTheme.typography.h3.fontSize,
        color = MaterialTheme.typography.h3.color,
        modifier = Modifier.padding(start = 10.dp, top = 20.dp))
}

@ExperimentalAnimationApi
@Composable
fun ExpandableSection(title: String, childComponent: @Composable () -> Unit) {
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .clickable {
                setIsExpanded(!isExpanded)
            }
            .fillMaxWidth()
            .background(White)
            .padding(top = 10.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = MaterialTheme.typography.h6.fontSize,
                color = MaterialTheme.typography.h6.color,
                letterSpacing = MaterialTheme.typography.h6.letterSpacing,
                fontFamily = MaterialTheme.typography.h6.fontFamily,
                modifier = Modifier.background(White)
            )
            AnimatedVisibility(visible = isExpanded) {
                Row(modifier = Modifier.background(White)) {
                    childComponent()
                }
            }
        }
    }
}

private fun sendMailToMe(): Intent {
    val myMailAddressFirstPart = "eflamm.ollivier"
    val myMailAddressSecondPart = "@gmail.com"
    val myMailAddressFull = myMailAddressFirstPart + myMailAddressSecondPart
    // FIXME choose only mail app
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, myMailAddressFull)
        putExtra(Intent.EXTRA_SUBJECT, "[NOTLELO]")
    }
    // TODO add information about the smartphone and the OS
    return intent
}

@Preview(showBackground = true)
@Composable
fun PreviewActivity() {
    val eventViewModel: IEventViewModel = MockEventViewModel()
    val userPreferencesViewModel: IUserPreferencesViewModel = MockUserPreferencesViewModel()
    NotleloTheme {
        SettingsScreen(rememberNavController(), eventViewModel, userPreferencesViewModel)
    }
}