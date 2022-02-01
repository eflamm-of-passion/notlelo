package io.eflamm.notlelo

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.*
import java.lang.NullPointerException
import java.lang.StringBuilder
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

// source : https://openclassrooms.com/en/courses/5779271-manage-your-data-to-have-a-100-offline-android-app-in-kotlin/5954921-create-a-file-on-external-storage
object StorageUtils {


    // read and write files

    fun setTextInStorage(rootDestination: File, context: Context, folderName: String, subFolderName: String, fileName: String, text: String): File {
        val file = createOrGetFileInsideSubFolder(rootDestination, folderName, subFolderName, fileName )
        return writeFile(context, file, text)
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

    fun zipFile(rootDestination: File, context: Context, folderName: String, fileName: String, fileToZip1: File): File {
        val zipFile = createOrGetFile(rootDestination, folderName, fileName)
        val zipOut = ZipOutputStream(FileOutputStream(zipFile))
        val fis = FileInputStream(fileToZip1)
        val origin = BufferedInputStream(fis)
        zipOut.putNextEntry(ZipEntry(fileToZip1.name))

        origin.copyTo(zipOut, 1024)
        origin.close()
        zipOut.close()

        return zipFile
    }

    fun zipFolder(rootDestination: File, outputFolderName: String, outputFileName: String, files: List<File>) {
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
    }

    fun getFileFromStorage(rootDestination: File, context: Context, folderName: String, fileName: String): File? {
        try {
            return createOrGetFile(rootDestination, folderName, fileName)
        } catch (e: NullPointerException) {
            Toast.makeText(context, context.getString(R.string.error_happened), Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun readFile(context: Context, file: File): String {
        val sb = StringBuilder()
        if(file.exists()) {
            try {
                val bufferedReader = file.bufferedReader()
                bufferedReader.useLines { lines ->
                    lines.forEach {
                        sb.append(it)
                        sb.append("/n")
                    }
                }
            } catch (e : IOException) {
                Toast.makeText(context, context.getString(R.string.error_happened), Toast.LENGTH_LONG).show()
            }
        }
        return sb.toString()
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
}