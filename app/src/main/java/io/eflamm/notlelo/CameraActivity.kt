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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import io.eflamm.notlelo.databinding.CameraActivityBinding
import io.eflamm.notlelo.model.Event
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


// source : https://developer.android.com/codelabs/camerax-getting-started#0

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: CameraActivityBinding
    private var imageCapture: ImageCapture? = null
    private var previewList: MutableList<File> = mutableListOf()
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private val _authority = "io.eflamm.notlelo.fileprovider"
    private lateinit var selectedEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CameraActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        fillMealSpinner()
    }

    private fun fillMealSpinner() {
        val spinner = this.findViewById<Spinner>(R.id.meal_spinner)
        val mealsAvailable = resources.getStringArray(R.array.camera_meals)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, mealsAvailable)
        spinner.adapter = adapter
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

        val productName = productNameInput.text
        val mealName = mealNameSpinner.selectedItem
        val selectedEvent = StorageUtils.getStringFromSharedPreferences(applicationContext, StorageUtils.SELECTED_EVENT)
        
        // TODO save the product in the database
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