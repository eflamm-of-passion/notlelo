package io.eflamm.notlelo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.*
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import android.content.Intent




// source : https://developer.android.com/codelabs/camerax-getting-started#0

class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

         if(allPermissionsGranted()) {
            startCamera()
         } else {
             ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
         }

        camera_capture_button.setOnClickListener { takePhoto() }
        camera_zip_button.setOnClickListener { zipAll("", "") }
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

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val appFolder = resources.getString(R.string.app_name)
        val dayFolder = "2021-12-2"
        val mealFolder = "dinner"
        val productFolder = "chocolate"
        val folders = "$appFolder/$dayFolder/$mealFolder/$productFolder"
        val photoOutputDirectory = createPhotoFolder(folders)
        val photoFile = File(
            photoOutputDirectory,
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
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d("takePhoto", msg)
                }
            }
        )
    }

    private fun createPhotoFolder(folders: String): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, folders).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    fun zipBack(directory: String, zipFile: String) {
        val inputFolder = resources.getString(R.string.app_name) + "/2021-12-2"
        val outputFilePath = resources.getString(R.string.app_name) + "/2021-12-2.zip"
        val outputFile = createPhotoFolder(outputFilePath)


//        val sourceFile = File(directory)
        val file = File(inputFolder)

            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { output ->
                    if(file.length() > 1) {
                        FileInputStream(file).use { input ->
                            BufferedInputStream(input).use { origin ->
                                val entry = ZipEntry(outputFilePath)
                                output.putNextEntry(entry)
                                origin.copyTo(output, 1024)
                            }
                        }
                    }
            }
    }

    fun zipAll(inputFilePath: String, outputFilePath: String) {
        // everything will happen in filesDir
        val inputFilePath = resources.getString(R.string.app_name) + "/test.jpg"
        val filePath1 = "test1.txt"
        val filePath2 = "test2.txt"
        val zipFilePath = "test.zip"
        val file1 = File(filesDir, filePath1)
        val file2 = File(filesDir, filePath2)
        val zipFile = File(filesDir, zipFilePath)



        file1.appendText("hello world")
        file2.appendText("foo bar")

        val zipOut = ZipOutputStream(FileOutputStream(zipFile.absolutePath))
        val fis = FileInputStream(file1)
        var origin = BufferedInputStream(fis)
        zipOut.putNextEntry(ZipEntry(file1.name))
        val bytes = ByteArray(1024)
        origin.buffered(1024).reader().forEachLine {
            zipOut.write(bytes)
        }
        origin.close()
        zipOut.close()

//        val inputAsString = FileInputStream(file1).bufferedReader().use {
//            Toast.makeText(applicationContext, it.readText(), Toast.LENGTH_LONG).show()
//        }



        val filesName = filesDir.listFiles().map { it.name }
//        Toast.makeText(applicationContext, filesName.toString(), Toast.LENGTH_LONG).show()
        Toast.makeText(applicationContext, zipFile.totalSpace.toString(), Toast.LENGTH_LONG).show()
        // the files is

        shareFile(zipFile)
    }

    private fun shareFile(fileToShare: File) {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        if(fileToShare.exists()) {
            intentShareFile.type = "application/zip";
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileToShare));

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    private fun startCamera() {
        val cameraProviderFuture =  ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

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

    private fun getOutputDirectory(): File {

        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}