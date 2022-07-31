package io.eflamm.notlelo

import android.content.Context
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path

// source : https://openclassrooms.com/en/courses/5779271-manage-your-data-to-have-a-100-offline-android-app-in-kotlin/5954921-create-a-file-on-external-storage
object StorageUtils {

    // read and write files

    fun insertPictureInTemporaryFolder(context: Context, eventName: String, dateAsString: String, mealName: String, productName: String, pictureFileName: String, pictureFile: File): File {
        // TODO construct some usable file architecture instead of each subfolder
        // TODO use the remaining arguments, or put them in a ordered list which would be cleaner instead of having some much arguments
        val temporaryFile = createOrGetFileInsideSubFolder(context.cacheDir, eventName, productName, pictureFileName)
        return Files.copy(Path(pictureFile.path), Path(temporaryFile.path), StandardCopyOption.REPLACE_EXISTING).toFile()
    }

    private fun createOrGetFile(destination: File, folderName: String, fileName: String): File {
        val folder = File(destination, folderName)
        folder.mkdirs()
        return File(folder, fileName)
    }

    private fun createOrGetFileInsideSubFolder(destination: File, folderName: String, subFolderName: String, fileName: String): File {
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
}