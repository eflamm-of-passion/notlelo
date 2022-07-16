package io.eflamm.notlelo

import android.content.Context
import android.widget.Toast
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path

// source : https://openclassrooms.com/en/courses/5779271-manage-your-data-to-have-a-100-offline-android-app-in-kotlin/5954921-create-a-file-on-external-storage
object StorageUtils {

    val SELECTED_EVENT = "SELECTED_EVENT"

    fun getStringFromSharedPreferences(context: Context, key: String): String? {
        return context?.getSharedPreferences(context.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE).getString(key, "")
    }

    fun saveStringToSharedPreferences(context: Context, key: String, value: String) {
        val sharedPreferences = context?.getSharedPreferences(context.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE) ?: return
        with (sharedPreferences.edit()) {
            putString(SELECTED_EVENT, value)
            apply()
        }
    }

    // read and write files

    fun setTextInStorage(rootDestination: File, context: Context, folderName: String, subFolderName: String, fileName: String, text: String): File {
        val file = createOrGetFileInsideSubFolder(rootDestination, folderName, subFolderName, fileName )
        return writeFile(context, file, text)
    }

    fun insertPictureInTemporaryFolder(context: Context, eventName: String, productName: String, pictureFileName: String, pictureFile: File): File {
        // TODO construct some usable file architecture instead of each subfolder
        val temporaryFile = createOrGetFileInsideSubFolder(context.cacheDir, eventName, productName, pictureFileName)
        return Files.copy(Path(pictureFile.path), Path(temporaryFile.path), StandardCopyOption.REPLACE_EXISTING).toFile()
    }

    fun createOrGetFile(destination: File, folderName: String, fileName: String): File {
        val folder = File(destination, folderName)
        folder.mkdirs()
        return File(folder, fileName)
    }

    fun createOrGetFileInsideSubFolder(destination: File, folderName: String, subFolderName: String, fileName: String): File {
        val folder = File(destination, folderName)
        folder.mkdirs()
        val subFolder = File(folder, subFolderName)
        subFolder.mkdirs()
        return File(subFolder, fileName)
    }

    fun zipFolder(rootDestination: File, outputFolderName: String, outputFileName: String, files: List<File>): File {
        val zipFile = createOrGetFile(rootDestination, outputFolderName, outputFileName)

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { output ->
            files.forEach { file ->
                if (file.length() > 1) {
                    FileInputStream(file).use { input ->
                        BufferedInputStream(input).use { origin ->
                            val entry = ZipEntry(file.path)
                            output.putNextEntry(entry)
                            origin.copyTo(output, 1024)
                        }
                    }
                }
            }
        }
        return zipFile
    }

    fun getFileFromStorage(rootDestination: File, context: Context, folderName: String, fileName: String): File? {
        try {
            return createOrGetFile(rootDestination, folderName, fileName)
        } catch (e: NullPointerException) {
            Toast.makeText(context, context.getString(R.string.error_happened), Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun writeFile(context: Context, file: File, text: String): File {
        try {
            file.parentFile.mkdirs()
            file.bufferedWriter().use {
                out -> out.write(text)
            }
        } catch (e: IOException) {
            Toast.makeText(context, context.getString(R.string.error_happened), Toast.LENGTH_LONG).show()
            return file
        }

        Toast.makeText(context, context.getString(R.string.file_saved), Toast.LENGTH_LONG).show()
        return file
    }

    fun clearCache(context: Context) {
        try {
            val cache = context.cacheDir
            deleteFolder(cache)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteFolder(file: File): Boolean {
        if (file.isDirectory) {
            val children = file.list()
            children.forEach {
                return deleteFolder(File(it))
            }
            return file.delete()
        } else if (file.isFile) {
            return file.delete()
        } else {
            return false
        }
    }
}