package io.eflamm.notlelo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.eflamm.notlelo.databinding.CameraActivityBinding
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.Product
import io.eflamm.notlelo.viewmodel.ProductViewModel
import io.eflamm.notlelo.viewmodel.ProductViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


// source : https://developer.android.com/codelabs/camerax-getting-started#0

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: CameraActivityBinding
    private var imageCapture: ImageCapture? = null
    private var previewList: MutableList<File> = mutableListOf()
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private val _authority = "io.eflamm.notlelo.fileprovider"
    private lateinit var selectedEvent: Event
    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((application as NotleloApplication).productRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         if(allPermissionsGranted()) {
            startCamera()
         } else {
             ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
         }

        val bundle: Bundle? = this.intent.extras
        selectedEvent = bundle?.getSerializable(getString(R.string.selected_event_key)) as Event

        val cameraCaptureButton = binding.buttonCameraCapture
        cameraCaptureButton.setOnClickListener { takePhoto() }
        // TODO give the file architecture
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSIONS) {
            if(allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permission not granted for the camera.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun onClickSaveProduct(view: View) {
        val saveProductModal = this.findViewById<LinearLayout>(R.id.layout_camera_save_product)
        saveProductModal.visibility = View.VISIBLE
    }

    fun onClickBackButton (view : View) {
        finish()
    }

    fun onClickEmptyPreviews (view: View) {
        val previewListLayout = this.findViewById<LinearLayout>(R.id.previewList)
        previewListLayout.removeAllViews()
        StorageUtils.clearCache(applicationContext) // delete the pictures in the cache
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

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

//        val appFolder = resources.getString(R.string.app_name)
        val cacheFolder = applicationContext.cacheDir.absolutePath
//        val dayFolder = "2021-12-2"
//        val mealFolder = "dinner"
//        val productFolder = "chocolate"
//        val folders = "$cacheFolder/$dayFolder/$mealFolder/$productFolder"
//        val photoOutputDirectory = createPhotoFolder(folders)
        val photoFile = File(
            cacheFolder,
            SimpleDateFormat(FILENAME_FORMAT, Locale.FRANCE).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    Log.e("takePhoto", "Photo capture failed: ${e.message}", e)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo captured succeeded: $savedUri"
                    previewList.add(photoFile)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d("takePhoto", msg)
                    displayPreviewList()
                }
            }
        )
    }

    private fun displayPreviewList() {
        // TODO refine this method
        val previewListLayout = this.findViewById<LinearLayout>(R.id.previewList)
        previewListLayout.removeAllViews()
        for(previewPhoto in previewList) {
            val myBitmap = BitmapFactory.decodeFile(previewPhoto.getAbsolutePath())
            val imageView = ImageView(this)
            imageView.setImageBitmap(myBitmap)
            previewListLayout.addView(imageView)
        }
    }

    private fun createPhotoFolder(folders: String): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, folders).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun startCamera() {
        val cameraProviderFuture =  ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val viewFinder = this.findViewById<PreviewView>(R.id.viewFinder)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("startCamera", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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
    val (isDisplayingSaveProductModal, setDisplayingSaveProductModal) = remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource(id = R.color.white))) {
        Box {
            CameraPreview(Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize()) {
                Row(Modifier.align(Alignment.Center)) {
                    CameraPermission()
                }
                Row(Modifier.align(Alignment.BottomCenter)) {
                    Button(onClick = { navController.navigateUp() }) {
                        Text(text = stringResource(id = R.string.camera_cancel), color = colorResource(id = R.color.black))
                    }
                    Button(onClick = { /*TODO*/ }) {
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

val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, executor)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermission() {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    when (cameraPermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        is PermissionStatus.Denied -> {
            Column {
                val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                    // If the user has denied the permission but the rationale can be shown,
                    // then gently explain why the app requires this permission
                    "The camera is important for this app. Please grant the permission."
                } else {
                    // If it's the first time the user lands on this feature, or the user
                    // doesn't want to be asked again for this permission, explain that the
                    // permission is required
                    "Camera permission required for this feature to be available. " +
                        "Please grant the permission"
                }
                Text(textToShow)
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text(text = "Request permission")
                }
            }
        }
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

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {
    val coroutineScope = rememberCoroutineScope()
    val lifeCycleOwner = LocalLifecycleOwner.current
    
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            coroutineScope.launch {
                val cameraProvider = context.getCameraProvider()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifeCycleOwner, cameraSelector, previewUseCase
                    )
                } catch (ex: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", ex)
                }
            }

            previewView
        }
    )
}