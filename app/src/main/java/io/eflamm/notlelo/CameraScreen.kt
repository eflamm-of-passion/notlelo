package io.eflamm.notlelo

import android.content.Context
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import io.eflamm.notlelo.model.EventWithDays
import io.eflamm.notlelo.ui.theme.Green
import io.eflamm.notlelo.ui.theme.LightGrey
import io.eflamm.notlelo.ui.theme.Red
import io.eflamm.notlelo.ui.theme.White
import io.eflamm.notlelo.viewmodel.ICameraViewModel
import io.eflamm.notlelo.viewmodel.IEventViewModel
import io.eflamm.notlelo.viewmodel.IUserPreferencesViewModel
import io.eflamm.notlelo.views.SelectListStyle
import io.eflamm.notlelo.views.SelectListView
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.*


// source : https://developer.android.com/codelabs/camerax-getting-started#0

@Composable
fun CameraScreen(navController: NavController, eventViewModel: IEventViewModel, cameraViewModel: ICameraViewModel, userPreferencesViewModel: IUserPreferencesViewModel) {
    val context = LocalContext.current
    val (isDisplayingSaveProductModal, setDisplayingSaveProductModal) = remember { mutableStateOf(false) }
    val resolutionFromUserPreference = userPreferencesViewModel.pictureResolution.observeAsState().value // TODO get the value synchronously probably, or pass it from the home page
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().setTargetResolution(mapSizeFromResolution(resolutionFromUserPreference ?: 480)).build()}
    val pictureLocations = cameraViewModel.cameraUiState.takenPicturesPath

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(id = R.color.white))) {
        Box {
            CameraPreview(imageCapture)
            Box(Modifier.fillMaxSize()) {
                Row(Modifier.align(Alignment.TopStart)) {
                    TakenPictures(isDisplayingSaveProductModal, pictureLocations, cameraViewModel)
                }
                Row(Modifier.align(Alignment.BottomCenter)) {
                    TextButton(onClick = { if(!isDisplayingSaveProductModal) navController.navigateUp() }) {
                        Icon(
                            Icons.Filled.ArrowBackIos,
                            contentDescription = stringResource(id = R.string.icon_desc_go_back),
                            modifier = Modifier.size(80.dp),
                            tint = if(!isDisplayingSaveProductModal) White else LightGrey
                        )
                    }
                    TextButton(onClick = { if(!isDisplayingSaveProductModal) takePhoto(context, imageCapture, cameraViewModel) }) {
                        Icon(
                            Icons.Filled.Camera,
                            contentDescription = stringResource(id = R.string.icon_desc_take_picture),
                            modifier = Modifier.size(80.dp),
                            tint = if(!isDisplayingSaveProductModal) White else LightGrey
                        )
                    }
                    TextButton(onClick = { if(pictureLocations.isNotEmpty() && !isDisplayingSaveProductModal) setDisplayingSaveProductModal(true) }) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(id = R.string.icon_desc_add_event),
                            modifier = Modifier.size(80.dp),
                            tint = if(pictureLocations.isNotEmpty() && !isDisplayingSaveProductModal) White else LightGrey
                        )
                    }
                }
            }
            if(isDisplayingSaveProductModal) {
                SaveProductModal(setDisplayingSaveProductModal, eventViewModel, cameraViewModel)
            }
        }
    }
}

private fun mapSizeFromResolution(resolution: Int): Size {
    return when(resolution) {
        240 -> Size(240,320)
        480 -> Size(480,640)
        720 -> Size(720, 1280 )
        else -> Size(240,320 )
    }
}

@Composable
fun CameraPreview(imageCapture: ImageCapture) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    val previewView = remember {
        PreviewView(context)
    }

    LaunchedEffect(CameraSelector.LENS_FACING_BACK) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView({previewView}, Modifier.fillMaxSize()){}
    }
}

@Composable
fun TakenPictures(disableButtons: Boolean, pictureLocations: List<String>, cameraViewModel: ICameraViewModel) {
    Box(
        Modifier
            .width(90.dp)
            ) {
        Column {
            if(pictureLocations.isNotEmpty()) {
                Row {
                    TextButton(onClick = { if(!disableButtons) cameraViewModel.removeAllPictures() }) {
                        Icon(
                            Icons.Filled.Cancel,
                            contentDescription = stringResource(id = R.string.icon_desc_add_event),
                            modifier = Modifier.size(80.dp),
                            tint = if(!disableButtons) White else LightGrey
                        )
                    }
                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top) {

                pictureLocations.forEach { pictureLocation ->
                    Row(modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.Start) {
                        TextButton(onClick = { if(!disableButtons) cameraViewModel.removePicture(pictureLocation) }) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(pictureLocation)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "",
                                placeholder = painterResource(R.drawable.ic_baseline_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SaveProductModal(setDisplayingSaveProductModal: (Boolean) -> Unit, eventViewModel: IEventViewModel, cameraViewModel: ICameraViewModel) {
    val appFolder = LocalContext.current.filesDir
    val preDefinedListOfMeals = stringArrayResource(id = R.array.camera_meals).toMutableList()

    val mealList = remember { preDefinedListOfMeals.toMutableStateList() }
    val (productName, setProductName) = remember { mutableStateOf("") }
    val (mealName, setMealName) = remember { mutableStateOf(preDefinedListOfMeals[0]) }

    val productSuggestions: List<String> = eventViewModel.getProductSuggestions(productName, 3).observeAsState().value ?: emptyList()

    val context = LocalContext.current
    val successMessage = stringResource(id = R.string.camera_product_saved)

    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Card(modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 100.dp, start = 20.dp, end = 20.dp)
            .heightIn(min = 350.dp)
            .clip(RoundedCornerShape(7.dp)),
            shape = RoundedCornerShape(7.dp),
        ) {
            Column(
                modifier = Modifier.background(color = White).padding(start = 15.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row {
                    Text(text = stringResource(id = R.string.camera_product_input_title), fontSize = MaterialTheme.typography.h5.fontSize, color = MaterialTheme.typography.h5.color)
                }
                Row {
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { setProductName(it) },
                        label = { Text(
                            stringResource(id = R.string.camera_product_input_label),
                            fontSize = 18.sp,
                            color = LightGrey
                        ) }
                    )
                }
                FlowRow(mainAxisAlignment = MainAxisAlignment.Center) {
                    productSuggestions.forEach { suggestionName ->
                        if(productName != suggestionName) {
                            TextButton(onClick = {
                                setProductName(suggestionName)
                            }) {
                                Text(text = processSuggestionName(suggestionName), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = White, letterSpacing = 0.1.sp,
                                    modifier = Modifier
                                        .background(LightGrey, CircleShape)
                                        .padding(start = 15.dp, end = 15.dp, top = 2.dp, bottom = 2.dp))
                            }
                        }
                    }
                }
                Row {
                    Text(text = stringResource(id = R.string.camera_meal_input_title), fontSize = MaterialTheme.typography.h5.fontSize, color = MaterialTheme.typography.h5.color)
                }
                Row {
                    SelectListView(mealName, mealList,
                        onSelect = { _, item ->
                            setMealName(item)
                        },
                        SelectListStyle(18.sp, 0.sp, FontWeight.Normal, 30.dp)
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Red),
                            onClick = {
                                setDisplayingSaveProductModal(false)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.camera_cancel), fontSize = MaterialTheme.typography.button.fontSize, color = MaterialTheme.typography.button.color)
                        }
                    }
                    Column {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Green),
                            onClick = {
                                setDisplayingSaveProductModal(false)
                                saveProduct(appFolder, productName, mealName, eventViewModel, cameraViewModel)
                                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()

                            }
                        ) {
                            Text(text = stringResource(id = R.string.camera_validate), fontSize = MaterialTheme.typography.button.fontSize, color = MaterialTheme.typography.button.color)
                        }
                    }
                }
            }
        }
    }
}

private fun processSuggestionName(suggestion: String): String {
    val maxLength = 14
    var processedSuggestion  = suggestion
    if(suggestion.length > maxLength) {
        processedSuggestion = processedSuggestion.substring(0, maxLength).plus("...")
    }
    return processedSuggestion
}

private fun takePhoto(context: Context, imageCapture: ImageCapture, cameraViewModel: ICameraViewModel ) {
    val cacheFolder = context.cacheDir.absolutePath
    val photoFile = File(
        cacheFolder,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.FRANCE).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // TODO externalise some of the logic in an appropriate class
    imageCapture.takePicture(
        outputOptions, ContextCompat.getMainExecutor(context), object: ImageCapture.OnImageSavedCallback {
            override fun onError(e: ImageCaptureException) {
                Log.e("takePhoto", "Photo capture failed: ${e.message}", e)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                cameraViewModel.addPicture(photoFile.absolutePath)
            }
        }
    )
}

private fun saveProduct(appFolder: File, productName: String, mealName: String, eventViewModel: IEventViewModel, cameraViewModel: ICameraViewModel) {
    val selectedEventWithDays: EventWithDays? = eventViewModel.uiState.selectedEventWithDays
    if(selectedEventWithDays != null) {
        // TODO probably move this part to the view model layer
        val picturePaths = cameraViewModel.cameraUiState.takenPicturesPath.toList() // toList is needed to create a copy of the list, otherwise the state list is lost in the process
        val savedPicturePaths = picturePaths.map { cachedPicturePath ->
            val sourcePath = Paths.get(cachedPicturePath)
            val targetFile = File(appFolder, SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.FRANCE).format(System.currentTimeMillis()) + ".jpg")
            val targetPath = Paths.get(targetFile.path)
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
            targetPath.toString()
        }
        eventViewModel.insertFullProduct(selectedEventWithDays.event.id, mealName, productName, savedPicturePaths)
        cameraViewModel.removeAllPictures()
    }
}