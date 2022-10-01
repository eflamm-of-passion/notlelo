package io.eflamm.notlelo

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import io.eflamm.notlelo.model.*
import io.eflamm.notlelo.ui.theme.LightGrey
import io.eflamm.notlelo.ui.theme.NotleloTheme
import io.eflamm.notlelo.ui.theme.Red
import io.eflamm.notlelo.ui.theme.White
import io.eflamm.notlelo.viewmodel.IEventViewModel
import io.eflamm.notlelo.viewmodel.MockEventViewModel
import io.eflamm.notlelo.views.HeaderView
import kotlinx.coroutines.CoroutineScope
import java.time.Month

@Composable
fun LibraryScreen(navController: NavController, eventViewModel: IEventViewModel){

    val context = LocalContext.current
    val libraryState: LibraryState = rememberLibraryState(eventViewModel = eventViewModel)
    var (selectedPicturePath, setSelectedPicturePath) = remember { mutableStateOf<String?>(null)} // TODO use state holder instead
    var eventWithProducts: EventWithDays? = eventViewModel.uiState.selectedEventWithDays
    val events: List<EventWithDays> = eventViewModel.allEventsWithDays.observeAsState().value ?: emptyList()

    // TODO move all the business logic to the state holder
    if(eventWithProducts != null) {
        val selectedEventId = eventWithProducts.event.id
        val updatedSelectedEvent = events.first { event -> event.event.id == selectedEventId }
        if(updatedSelectedEvent == null) {
            // selected event not found
            eventViewModel.updateSelectedEventWithDays(events[0])
            eventWithProducts = events[0]
        } else {
            eventViewModel.updateSelectedEventWithDays(updatedSelectedEvent)
            eventWithProducts = updatedSelectedEvent
        }
    }

    Column(
        // scaffoldState = libraryState.scaffoldState,
        modifier = Modifier.fillMaxSize()
        .background(color = colorResource(id = R.color.white))) {
        HeaderView(
            navController,
            eventWithProducts?.event?.name ?: "",
            )
        {
            ShareEventButton(context, eventWithProducts, eventViewModel)
        }
        if (eventWithProducts?.days != null) {
            Days(eventWithProducts.days, eventViewModel, setSelectedPicturePath)
        }
    }
    if (selectedPicturePath != null) {
        DisplayFullscreenPicture(selectedPicturePath!!, eventViewModel, setSelectedPicturePath)
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Days(days: List<DayWithMeals>, eventViewModel: IEventViewModel, setSelectedPicturePath: (path: String) -> Unit) {

    HorizontalPager(count = days.size) { page ->
        val dayWithMeals = days[page]
        val monthAsString = when(dayWithMeals.day.date.month) {
            Month.JANUARY -> stringResource(id = R.string.month_january)
            Month.FEBRUARY -> stringResource(id = R.string.month_february)
            Month.MARCH -> stringResource(id = R.string.month_march)
            Month.APRIL -> stringResource(id = R.string.month_april)
            Month.MAY -> stringResource(id = R.string.month_may)
            Month.JUNE -> stringResource(id = R.string.month_june)
            Month.JULY -> stringResource(id = R.string.month_july)
            Month.AUGUST -> stringResource(id = R.string.month_august)
            Month.SEPTEMBER -> stringResource(id = R.string.month_september)
            Month.OCTOBER -> stringResource(id = R.string.month_october)
            Month.NOVEMBER -> stringResource(id = R.string.month_november)
            Month.DECEMBER -> stringResource(id = R.string.month_december)
            else -> ""
        }
        val dateAsString = "${dayWithMeals.day.date.dayOfMonth} $monthAsString"

        Box {
            Row {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top) {
                    Row {
                        Text(
                            text = dateAsString,
                            color = MaterialTheme.typography.h3.color,
                            fontSize = MaterialTheme.typography.h3.fontSize
                        )
                    }
                    Meals(dayWithMeals.meals, eventViewModel, setSelectedPicturePath)
                }
            }
        }
        Box(Modifier.fillMaxSize()) {

            Row(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(bottom = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(
                    Icons.Filled.ArrowBackIos,
                    contentDescription = stringResource(id = R.string.icon_desc_go_back),
                    modifier = Modifier
                        .width(30.dp)
                        .height(40.dp),
                    tint = if(page > 0) LightGrey else Color.Transparent
                )
                Icon(
                    Icons.Outlined.ArrowForwardIos,
                    contentDescription = stringResource(id = R.string.icon_desc_add_event),
                    modifier = Modifier
                        .width(30.dp)
                        .height(40.dp),
                    tint = if(page < days.size - 1) LightGrey else Color.Transparent
                )
            }
        }
    }
}

@Composable
fun Meals(meals: List<MealWithProducts>, eventViewModel: IEventViewModel, setSelectedPicturePath: (path: String) -> Unit) {
    LazyColumn {
        items(meals) { mealWithProducts ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row {
                        Text(
                            text = mealWithProducts.meal.name,
                            color = MaterialTheme.typography.h5.color,
                            fontSize = MaterialTheme.typography.h5.fontSize,
                        )
                    }
                    Products(mealWithProducts.products, eventViewModel, setSelectedPicturePath)
                }
            }
        }
    }
}

@Composable
fun Products(products: List<ProductWithPictures>, eventViewModel: IEventViewModel, setSelectedPicturePath: (path: String) -> Unit) {
    products.forEach { productWithPictures ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = CenterVertically) {
                Text(
                    text = productWithPictures.product.name,
                    color = MaterialTheme.typography.h6.color,
                    fontSize = MaterialTheme.typography.h6.fontSize,
                )
                DeleteProductButton(productWithPictures, eventViewModel)
            }
            Pictures(productWithPictures.pictures, eventViewModel, setSelectedPicturePath)
        }
}

@Composable
fun Pictures(pictures: List<Picture>, eventViewModel: IEventViewModel, setSelectedPicturePath: (path: String) -> Unit) {
    FlowRow {
        pictures.forEach { picture ->
            TextButton(onClick = {
                setSelectedPicturePath(picture.path)
            }) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(picture.path)
                        .crossfade(true)
                        .build(),
                    contentDescription = "",
                    placeholder = painterResource(R.drawable.ic_baseline_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(7.dp))
                )
            }
        }
    }
}

@Composable
fun ShareEventButton(context: Context, eventToShare: EventWithDays?, eventViewModel: IEventViewModel) {
    TextButton( onClick = {
        if (eventToShare != null) {
            val intent = eventViewModel.shareEvent(context, eventToShare)
            context.startActivity(intent)
        }
        // TODO else display toast to indicate the event are not yet loaded
    }) {
        Icon(
            Icons.Filled.Share,
            contentDescription = stringResource(id = R.string.icon_desc_share_event),
            modifier = Modifier.size(35.dp),
            tint = White
        )
    }
}

@Composable
fun DeleteProductButton(productWithPictures: ProductWithPictures, eventViewModel: IEventViewModel) {
    TextButton( onClick = {
        eventViewModel.deleteProduct(productWithPictures.product)
    }) {
        Icon(
            Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.icon_desc_delete_event),
            modifier = Modifier.size(30.dp),
            tint = Red
        )
    }
}

@Composable
fun DisplayFullscreenPicture(pictureToDisplayPath: String, eventViewModel: IEventViewModel, setSelectedPicturePath: (path: String?) -> Unit) {
    TextButton(modifier = Modifier.fillMaxSize(), onClick = { setSelectedPicturePath(null) }) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pictureToDisplayPath)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                placeholder = painterResource(R.drawable.ic_baseline_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .clip(RoundedCornerShape(7.dp))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val eventViewModel: IEventViewModel = MockEventViewModel()
    NotleloTheme {
        LibraryScreen(rememberNavController(), eventViewModel)
    }
}

class LibraryState(
    val scaffoldState: ScaffoldState,
    private val eventViewModel: IEventViewModel,
    private val coroutineScope: CoroutineScope
) {
    var selectedPicturePath: String? = null
        private set

    fun updateSelectedPicturePath(path: String?) {
        selectedPicturePath = path
    }
}

@Composable
fun rememberLibraryState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    eventViewModel: IEventViewModel = viewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember {
    LibraryState(
        scaffoldState,
        eventViewModel,
        coroutineScope
    )
}