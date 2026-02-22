package com.hdw.bookmarker.core.data.file

import android.content.Context
import android.net.Uri
import com.hdw.bookmarker.core.model.file.error.ContentFileError
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentFileManager @Inject constructor(@param:ApplicationContext private val context: Context) {

    fun readUtf8Text(uri: Uri): ContentFileResult<String> = when (val bytesResult = readBytes(uri)) {
        is ContentFileResult.Success -> {
            val text = bytesResult.data.toString(Charsets.UTF_8)
            if (text.isBlank()) {
                ContentFileResult.Failure(error = ContentFileError.EMPTY_CONTENT)
            } else {
                ContentFileResult.Success(data = text)
            }
        }

        is ContentFileResult.Failure -> bytesResult
    }

    fun getRawFileHash(uri: Uri): ContentFileResult<String> = when (val bytesResult = readBytes(uri)) {
        is ContentFileResult.Success -> {
            val digest = MessageDigest.getInstance("SHA-256").digest(bytesResult.data)
            val hash = digest.joinToString(separator = "") { byte -> "%02x".format(byte) }
            ContentFileResult.Success(data = hash)
        }

        is ContentFileResult.Failure -> bytesResult
    }

    private fun readBytes(uri: Uri): ContentFileResult<ByteArray> {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)
                ?.use { it.readBytes() }
                ?: return ContentFileResult.Failure(error = ContentFileError.INVALID_URI)

            if (bytes.isEmpty()) {
                return ContentFileResult.Failure(error = ContentFileError.EMPTY_CONTENT)
            }

            ContentFileResult.Success(data = bytes)
        } catch (exception: SecurityException) {
            Timber.e(exception, "Permission denied while reading file. uri=%s", uri)
            ContentFileResult.Failure(
                error = ContentFileError.PERMISSION_DENIED,
                message = exception.message,
            )
        } catch (exception: FileNotFoundException) {
            Timber.e(exception, "File not found while reading file. uri=%s", uri)
            ContentFileResult.Failure(
                error = ContentFileError.FILE_NOT_FOUND,
                message = exception.message,
            )
        } catch (exception: IOException) {
            Timber.e(exception, "I/O error while reading file. uri=%s", uri)
            ContentFileResult.Failure(
                error = ContentFileError.IO_ERROR,
                message = exception.message,
            )
        } catch (exception: IllegalArgumentException) {
            Timber.e(exception, "Invalid uri while reading file. uri=%s", uri)
            ContentFileResult.Failure(
                error = ContentFileError.INVALID_URI,
                message = exception.message,
            )
        } catch (exception: Exception) {
            Timber.e(exception, "Unknown error while reading file. uri=%s", uri)
            ContentFileResult.Failure(
                error = ContentFileError.UNKNOWN,
                message = exception.message,
            )
        }
    }
}
