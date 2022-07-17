package io.eflamm.notlelo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.eflamm.notlelo.model.*
import io.eflamm.notlelo.ui.theme.NotleloTheme
import io.eflamm.notlelo.viewmodel.IEventViewModel
import io.eflamm.notlelo.viewmodel.MockEventViewModel
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
fun LibraryView(navController: NavController, eventViewModel: IEventViewModel){

    val context = LocalContext.current
    val event: Event? = eventViewModel.uiState.selectedEvent
    val eventWithProducts: EventWithDays? =
        event?.let { eventViewModel.eventWithProducts(it.id).observeAsState().value }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(id = R.color.white))) {
        HeaderView(
            navController,
            stringResource(id = R.string.home_library),
            ShareEventButton(context, eventWithProducts, eventViewModel)
            )
        if (eventWithProducts?.days != null) {
            Days(eventWithProducts.days)
        }
    }
}

@Composable
fun Days(days: List<DayWithMeals>) {
    days.forEach { dayWithMeals ->
        Row {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top) {
                Row {
                    Text(text = dayWithMeals.day.date.toString(),
                        color = colorResource(id = R.color.secondary),
                        fontSize = 35.sp,
                    )
                }
                Meals(dayWithMeals.meals)
            }
        }
    }
}

@Composable
fun Meals(meals: List<MealWithProducts>) {
    meals.forEach { mealWithProducts ->
        Row {
            Column {
                Row {
                    Text(text = mealWithProducts.meal.name,
                        color = colorResource(id = R.color.secondary),
                        fontSize = 30.sp,
                    )
                }
                Products(mealWithProducts.products)
            }
        }
    }
}

@Composable
fun Products(products: List<ProductWithPictures>) {
    Row {
        products.forEach { productWithPictures ->
            Column {
                Row {
                    Text(text = productWithPictures.product.name,
                        color = colorResource(id = R.color.secondary),
                        fontSize = 25.sp,
                    )
                }
                Pictures(productWithPictures.pictures)
            }
        }
    }
}

@Composable
fun Pictures(pictures: List<Picture>) {
    Row(modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.Start) {
        pictures.forEach { picture ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(picture.path)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(125.dp)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(7.dp))
            )
        }
    }
}

@Composable
fun ShareEventButton(context: Context, eventToShare: EventWithDays?, eventViewModel: IEventViewModel) {
    Button( onClick = {
        if (eventToShare != null) {
            val intent = eventViewModel.shareEvent(context, eventToShare)
            context.startActivity(intent)
        }
        // TODO else display toast to indicate the event are not yet loaded
    }) {
        Text("Partager")
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