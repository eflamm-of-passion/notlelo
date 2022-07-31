package io.eflamm.notlelo

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.eflamm.notlelo.model.*
import io.eflamm.notlelo.ui.theme.LightGrey
import io.eflamm.notlelo.ui.theme.NotleloTheme
import io.eflamm.notlelo.ui.theme.Red
import io.eflamm.notlelo.ui.theme.White
import io.eflamm.notlelo.viewmodel.IEventViewModel
import io.eflamm.notlelo.viewmodel.MockEventViewModel
import io.eflamm.notlelo.views.HeaderView
import kotlinx.coroutines.launch
import java.time.Month

@Composable
fun LibraryView(navController: NavController, eventViewModel: IEventViewModel){

    val context = LocalContext.current
    val event: Event? = eventViewModel.uiState.selectedEvent
    val fullScreenPicture: Picture? = eventViewModel.uiState.selectedPicture
    val eventWithProducts: EventWithDays? =
        event?.let { eventViewModel.eventWithProducts(it.id).observeAsState().value }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(id = R.color.white))) {
        HeaderView(
            navController,
            event?.name ?: "",
            )
        {
            ShareEventButton(context, eventWithProducts, eventViewModel)
        }
        if (eventWithProducts?.days != null) {
            Days(eventWithProducts.days, eventViewModel)
        }
    }
    if (fullScreenPicture != null)
        DisplayFullscreenPicture(fullScreenPicture, eventViewModel)

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Days(days: List<DayWithMeals>, eventViewModel: IEventViewModel) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    HorizontalPager(count = days.size, state = pagerState) { page ->
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
                    Meals(dayWithMeals.meals, eventViewModel)
                }
            }
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 20.dp), horizontalArrangement = Arrangement.SpaceAround) {
                OutlinedButton(
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = if(page > 0) MaterialTheme.colors.primary else LightGrey),
                    onClick = { if(page > 0) scope.launch { pagerState.scrollToPage(page - 1, 1F) } } )  {
                    Icon(
                        Icons.Filled.ArrowBackIos,
                        contentDescription = stringResource(id = R.string.icon_desc_go_back),
                        modifier = Modifier
                            .width(30.dp)
                            .height(40.dp),
                        tint = White
                    )
                }
                OutlinedButton(
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(backgroundColor = if(page < days.size - 1) MaterialTheme.colors.primary else LightGrey),
                    onClick = { if(page < days.size - 1) scope.launch { pagerState.scrollToPage(page + 1, 1F) } } ) {
                    Icon(
                        Icons.Outlined.ArrowForwardIos,
                        contentDescription = stringResource(id = R.string.icon_desc_add_event),
                        modifier = Modifier
                            .width(30.dp)
                            .height(40.dp),
                        tint = White
                    )
                }
            }
        }
    }
}

@Composable
fun Meals(meals: List<MealWithProducts>, eventViewModel: IEventViewModel) {
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
                    Products(mealWithProducts.products, eventViewModel)
                }
            }
        }
    }
}

@Composable
fun Products(products: List<ProductWithPictures>, eventViewModel: IEventViewModel) {
    Row {
        products.forEach { productWithPictures ->
            Column {
                Row {
                    Text(
                        text = productWithPictures.product.name,
                        color = MaterialTheme.typography.h6.color,
                        fontSize = MaterialTheme.typography.h6.fontSize,
                    )
                    DeleteProductButton(productWithPictures, eventViewModel)
                }
                Pictures(productWithPictures.pictures, eventViewModel)
            }
        }
    }
}

@Composable
fun Pictures(pictures: List<Picture>, eventViewModel: IEventViewModel) {
    Row(modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.Start) {
        pictures.forEach { picture ->
            TextButton(onClick = {
                eventViewModel.updateSelectedPicture(picture)
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
                        .size(125.dp)
                        .padding(5.dp)
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
            tint = colorResource(id = R.color.primary)
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
fun DisplayFullscreenPicture(pictureToDisplay: Picture, eventViewModel: IEventViewModel) {
    TextButton(modifier = Modifier.fillMaxSize(), onClick = { eventViewModel.updateSelectedPicture(null) }) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pictureToDisplay.path)
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
        LibraryView(rememberNavController(), eventViewModel)
    }
}