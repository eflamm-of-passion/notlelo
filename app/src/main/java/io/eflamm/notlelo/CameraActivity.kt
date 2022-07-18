package io.eflamm.notlelo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.ui.theme.Green
import io.eflamm.notlelo.ui.theme.Red
import io.eflamm.notlelo.viewmodel.ICameraViewModel
import io.eflamm.notlelo.viewmodel.IEventViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


// source : https://developer.android.com/codelabs/camerax-getting-started#0

class CameraActivity : AppCompatActivity() {

    private lateinit var outputDirectory: File
    private val _authority = "io.eflamm.notlelo.fileprovider"
    private lateinit var selectedEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // TODO give the file architecture
        outputDirectory = getOutputDirectory()

    }


    fun zipAll(inputFilePath: String, outputFilePath: String) {
        val textFile1 = StorageUtils.setTextInStorage(filesDir, this, "notlelo", "camp1", "test1.txt", "hello world")
        val textFile2 = StorageUtils.setTextInStorage(filesDir, this, "notlelo", "camp2", "test2.txt", "hello world")
//        val zipFile = StorageUtils.zipFile(filesDir, this, "notlelo", "test.zip", textFile)
        val zipFile = StorageUtils.zipFolder(filesDir, "notlelo", "test.zip", listOf(textFile1, textFile2))

//        val filesName = filesDir.listFiles().map { it.name }
//        Toast.makeText(applicationContext, filesName.toString(), Toast.LENGTH_LONG).show()
//        Toast.makeText(applicationContext, zipFile.totalSpace.toString(), Toast.LENGTH_LONG).show()
//        Toast.makeText(applicationContext, StorageUtils.getTextFromStorage(filesDir, this, "notlelo", "test.txt"), Toast.LENGTH_LONG).show()

        // the files is

        shareFile(filesDir, this, "notlelo", "test.zip")
    }

    private fun shareFile(rootDestination: File, context: Context, folderName: String, fileName: String ) {
        val internalFile =
            StorageUtils.getFileFromStorage(rootDestination, context, folderName, fileName)
        val contentUri = FileProvider.getUriForFile(context, _authority, internalFile!!)
                Toast.makeText(applicationContext, "internalFile " +internalFile.totalSpace.toString(), Toast.LENGTH_LONG).show()
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "application/zip"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

        startActivity(Intent.createChooser(sharingIntent, getString(R.string.notlelo_share)))
    }



    private fun getOutputDirectory(): File {

        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}

@Composable
fun CameraView(navController: NavController, eventViewModel: IEventViewModel, cameraViewModel: ICameraViewModel) {
    val context = LocalContext.current
    val (isDisplayingSaveProductModal, setDisplayingSaveProductModal) = remember { mutableStateOf(false) }
    val outputDirectory = File(LocalContext.current.cacheDir.absolutePath)
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(id = R.color.white))) {
        Box {
            CameraPreview(imageCapture)
            Box(Modifier.fillMaxSize()) {
                Row(Modifier.align(Alignment.TopStart)) {
                    TakenPictures(cameraViewModel)
                }
                Row(Modifier.align(Alignment.Center)) {
                    AskCameraPermissionRationale()
                }
                Row(Modifier.align(Alignment.BottomCenter)) {
                    TextButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Filled.ArrowBackIos,
                            contentDescription = stringResource(id = R.string.icon_desc_go_back),
                            modifier = Modifier.size(80.dp),
                            tint = colorResource(id = R.color.white)
                        )
                    }
                    TextButton(onClick = { takePhoto(context, imageCapture, cameraViewModel) }) {
                        Icon(
                            Icons.Filled.Camera,
                            contentDescription = stringResource(id = R.string.icon_desc_take_picture),
                            modifier = Modifier.size(80.dp),
                            tint = colorResource(id = R.color.white)
                        )
                    }
                    TextButton(onClick = { setDisplayingSaveProductModal(true) }) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(id = R.string.icon_desc_add_event),
                            modifier = Modifier.size(80.dp),
                            tint = colorResource(id = R.color.white)
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
fun TakenPictures(cameraViewModel: ICameraViewModel) {
    val pictureLocations = cameraViewModel.cameraUiState.takenPicturesPath

    Box(
        Modifier
            .width(75.dp)
            ) {
        Column {
            if(pictureLocations.size > 0) {
                Row {
                    TextButton(onClick = { cameraViewModel.removeAllPictures() }) {
                        Icon(
                            Icons.Filled.Cancel,
                            contentDescription = stringResource(id = R.string.icon_desc_add_event),
                            modifier = Modifier.size(80.dp),
                            tint = colorResource(id = R.color.white)
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
                    // TODO update the list when a photo is added
                    Row(modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.Start) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(pictureLocation)
                                .crossfade(true)
                                .build(),
                            contentDescription = "",
                            placeholder = painterResource(R.drawable.ic_launcher_foreground),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(64.dp).clip(CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SaveProductModal(setDisplayingSaveProductModal: (Boolean) -> Unit, eventViewModel: IEventViewModel, cameraViewModel: ICameraViewModel) {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {

        val (productName, setProductName) = remember { mutableStateOf("") }
        val (mealName, setMealName) = remember { mutableStateOf("") }

        Column(modifier = Modifier
            .align(Alignment.Center)
            .width(400.dp)
            .height(250.dp)
            .background(color = colorResource(id = R.color.white))) {
            Row {
                Text(text = stringResource(id = R.string.camera_product_input_label), fontSize = MaterialTheme.typography.h5.fontSize, color = MaterialTheme.typography.h5.color)
            }
            Row {
                TextField(
                    value = productName,
                    onValueChange = { setProductName(it) },
                    modifier = Modifier.width(300.dp),
//                    label = {Text(stringResource(id = R.string.camera_product_input_label))},
                    colors = TextFieldDefaults.textFieldColors( textColor = colorResource(id = android.R.color.darker_gray))
                )
            }
            Row {
                Text(text = stringResource(id = R.string.camera_meal_input_label), fontSize = MaterialTheme.typography.h5.fontSize, color = MaterialTheme.typography.h5.color)
            }
            Row {
                /* TODO list of meals*/
                TextField(
                    value = mealName,
                    onValueChange = { setMealName(it) },
                    modifier = Modifier.width(300.dp),
                    colors = TextFieldDefaults.textFieldColors( textColor = colorResource(id = android.R.color.darker_gray))
                )
            }
            Row {
                // TODO disable the button if there is not selected event, if the field are not set
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
                            saveProduct(productName, mealName, eventViewModel, cameraViewModel)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.camera_validate), fontSize = MaterialTheme.typography.button.fontSize, color = MaterialTheme.typography.button.color)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskCameraPermissionRationale() {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    when(cameraPermissionState.status) {
        PermissionStatus.Granted -> {

        }
        is PermissionStatus.Denied -> {
            Column(modifier = Modifier.background(Color.White)) {
                val textToShow = if(cameraPermissionState.status.shouldShowRationale) {
                    // User has denied
                    "This feature is required"
                } else {
                    "Please grant the permission to use the camera"
                }
                Text(text = textToShow)
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text(text = "Request permission")
                }
            }
        }
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({continuation.resume(cameraProvider.get())}, ContextCompat.getMainExecutor(this))
    }
}


private fun takePhoto(context: Context, imageCapture: ImageCapture, cameraViewModel: ICameraViewModel ) {
    // TODO decrease the photo quality, because it takes too much space
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

private fun saveProduct(productName: String, mealName: String, eventViewModel: IEventViewModel, cameraViewModel: ICameraViewModel) {
    val selectedEvent: Event? = eventViewModel.uiState.selectedEvent
    if(selectedEvent != null) {
        val picturePaths = cameraViewModel.cameraUiState.takenPicturesPath.toList() // toList is needed to create a copy of the list, otherwise the state list is lost in the process
        eventViewModel.insertFullProduct(selectedEvent.id, mealName, productName, picturePaths)
        cameraViewModel.removeAllPictures()
    }
}