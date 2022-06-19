package io.eflamm.notlelo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.Product
import io.eflamm.notlelo.viewmodel.ProductViewModel
import io.eflamm.notlelo.viewmodel.ProductViewModelFactory
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
    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((application as NotleloApplication).productRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // TODO give the file architecture
        outputDirectory = getOutputDirectory()

    }



    fun onClickCancelSaveProduct(view: View) {
        // TODO clean the fields
        val modal = findViewById<LinearLayout>(R.id.layout_camera_save_product)
        modal.visibility = View.GONE
    }

    fun onClickValidateSaveProduct(view: View) {
        val productNameInput = findViewById<EditText>(R.id.input_product_name)
        val mealNameSpinner = findViewById<Spinner>(R.id.meal_spinner)

        val productName = productNameInput.text.toString()
        val mealName = mealNameSpinner.selectedItem.toString()

        val productToSave = Product(productName, mealName, selectedEvent.id)
        productViewModel.insert(productToSave)
        // TODO move the pictures from cache to internal storage

        // remove the previews
        val previewListLayout = this.findViewById<LinearLayout>(R.id.previewList)
        previewListLayout.removeAllViews()
        StorageUtils.clearCache(applicationContext)

        // TODO clean the fields

        // close the modal
        val modal = findViewById<LinearLayout>(R.id.layout_camera_save_product)
        modal.visibility = View.GONE
    }




    private fun createPhotoFolder(folders: String): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, folders).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
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
fun CameraView(navController: NavController) {
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
                Row(Modifier.align(Alignment.Center)) {
                    AskCameraPermissionRationale()
                }
                Row(Modifier.align(Alignment.BottomCenter)) {
                    Button(onClick = { navController.navigateUp() }) {
                        Text(text = stringResource(id = R.string.camera_cancel), color = colorResource(id = R.color.black))
                    }
                    Button(onClick = { takePhoto(context, imageCapture) }) {
                        Text(text = "take picture", color = colorResource(id = R.color.black))
                    }
                    Button(onClick = { setDisplayingSaveProductModal(true) }) {
                        Text(text = stringResource(id = R.string.camera_validate), color = colorResource(id = R.color.black))
                    }
                }
            }
            if(isDisplayingSaveProductModal) {
                SaveProductModal(setDisplayingSaveProductModal)
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
fun SaveProductModal(setDisplayingSaveProductModal: (Boolean) -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Column(modifier = Modifier
            .align(Alignment.Center)
            .width(400.dp)
            .height(200.dp)
            .background(color = colorResource(id = R.color.white))) {
            Row {
                Text(text = stringResource(id = R.string.camera_product_input_label), color = colorResource(
                    id = R.color.secondary
                ))
            }
            Row {
                TextField(
                    value = "",
                    onValueChange = {/* TODO */ },
                    modifier = Modifier.width(300.dp),
//                    label = {Text(stringResource(id = R.string.camera_product_input_label))},
                    colors = TextFieldDefaults.textFieldColors( textColor = colorResource(id = android.R.color.darker_gray))
                )
            }
            Row {
                Text(text = stringResource(id = R.string.camera_meal_input_label), color = colorResource(
                    id = R.color.secondary
                ))
            }
            Row {
                /* TODO list of meals*/
            }
            Row {
                Column {
                    Button(onClick = { setDisplayingSaveProductModal(false) }) {
                        Text(text = stringResource(id = R.string.camera_cancel), color = colorResource(id = R.color.black))
                    }
                }
                Column {
                    Button(onClick = { setDisplayingSaveProductModal(false) }) {
                        Text(text = stringResource(id = R.string.camera_validate), color = colorResource(id = R.color.black))
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


private fun takePhoto(context: Context, imageCapture: ImageCapture ) {
    imageCapture.takePicture(ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(imageProxy: ImageProxy) {
            // TODO externalise some of the logic in an appropriate class
            val cacheFolder = context.cacheDir.absolutePath
            val photoFile = File(
                cacheFolder,
                SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.FRANCE).format(System.currentTimeMillis()) + ".jpg"
            )

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(context), object: ImageCapture.OnImageSavedCallback {
                    override fun onError(e: ImageCaptureException) {
                        Log.e("takePhoto", "Photo capture failed: ${e.message}", e)
                    }

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)
                        val msg = "Photo captured succeeded: $savedUri"
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        Log.d("takePhoto", msg)
                    }
                }
            )
        }
        override fun onError(exception: ImageCaptureException) {
            TODO("Not yet implemented")
        }
    })
}