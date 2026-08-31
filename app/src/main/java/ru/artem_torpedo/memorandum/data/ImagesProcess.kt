package ru.artem_torpedo.memorandum.data

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


class ImagesProcess @Inject constructor(
    @ApplicationContext val context: Context,
) {
    val format = SimpleDateFormat("HHmmss_ddMMyyyy", Locale.getDefault())
    val imagesDir: File = context.filesDir

    suspend fun internalStorageAdd(url: String): String {
        val result: String = format.format(System.currentTimeMillis())
        val fileName = "IMG_${result}.jpg"
        val file = File(imagesDir, fileName)
        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(url.toUri()).use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
        }
        return file.absolutePath
    }

    suspend fun internalStorageDelete(url: String) {
        withContext(Dispatchers.IO) {
            if (isInternalFile(url)) {
                File(url).delete()
            }
        }
    }

    fun isInternalFile(url: String): Boolean {
        return url.startsWith(imagesDir.absolutePath)
    }
}