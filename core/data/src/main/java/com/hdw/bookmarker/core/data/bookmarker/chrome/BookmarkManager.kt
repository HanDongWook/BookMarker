package com.hdw.bookmarker.core.data.bookmarker.chrome

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class BookmarkManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    /**
     * Chrome 북마크 관리자 열기
     */
    fun openChromeBookmarks(): Boolean {
        return try {
            // SAF(Storage Access Framework)를 사용하여 파일 선택기 열기
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "text/html" // HTML 파일만 필터링
                addCategory(Intent.CATEGORY_OPENABLE)
            }
//            startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
            false
        } catch (e: Exception) {
            false
        }
    }
//    private val parser = BookmarkParser()
//
//    companion object {
//        const val PICK_BOOKMARK_FILE = 1001
//        const val CHROME_PACKAGE = "com.android.chrome"
//    }
//
//    /**
//     * Chrome 북마크 내보내기 안내
//     */
//    fun showExportInstructions(): String {
//        return """
//            Chrome 북마크 내보내기 방법:
//
//            1. Chrome 브라우저를 엽니다
//            2. 오른쪽 상단 메뉴(⋮)를 탭합니다
//            3. '북마크' → '북마크 관리자'를 선택합니다
//            4. 오른쪽 상단 메뉴(⋮)를 탭합니다
//            5. '북마크 내보내기'를 선택합니다
//            6. 파일이 다운로드 폴더에 저장됩니다
//            7. 아래 버튼을 눌러 내보낸 파일을 선택하세요
//        """.trimIndent()
//    }
//
//    /**
//     * 파일 선택기 열기 Intent
//     */
//    fun createFilePickerIntent(): Intent {
//        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "text/html"
//            // 또는 모든 파일
//            // type = "*/*"
//            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/html", "text/*"))
//        }
//    }
//
//    /**
//     * 선택한 파일에서 북마크 로드
//     */
//    suspend fun loadBookmarksFromUri(uri: Uri): Result<List<Bookmark>> {
//        return try {
//            val bookmarks = context.contentResolver.openInputStream(uri)?.use { inputStream ->
//                parser.parseBookmarkHtml(inputStream)
//            } ?: emptyList()
//
//            Result.success(bookmarks)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    /**
//     * 폴더 구조 포함하여 로드
//     */
//    suspend fun loadBookmarksWithFolders(uri: Uri): Result<BookmarkFolder> {
//        return try {
//            val folder = context.contentResolver.openInputStream(uri)?.use { inputStream ->
//                parser.parseBookmarkWithFolders(inputStream)
//            } ?: BookmarkFolder("북마크")
//
//            Result.success(folder)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    /**
//     * 북마크를 HTML 파일로 내보내기
//     */
//    fun exportBookmarksToHtml(bookmarks: List<Bookmark>, fileName: String = "bookmarks.html"): File {
//        val file = File(context.getExternalFilesDir(null), fileName)
//
//        val html = buildString {
//            appendLine("<!DOCTYPE NETSCAPE-Bookmark-file-1>")
//            appendLine("<!-- This is an automatically generated file.")
//            appendLine("     It will be read and overwritten.")
//            appendLine("     DO NOT EDIT! -->")
//            appendLine("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">")
//            appendLine("<TITLE>Bookmarks</TITLE>")
//            appendLine("<H1>Bookmarks</H1>")
//            appendLine("<DL><p>")
//
//            bookmarks.forEach { bookmark ->
//                appendLine("    <DT><A HREF=\"${bookmark.url}\" ADD_DATE=\"${bookmark.addDate}\">${bookmark.title}</A>")
//            }
//
//            appendLine("</DL><p>")
//        }
//
//        FileOutputStream(file).use { output ->
//            output.write(html.toByteArray())
//        }
//
//        return file
//    }
//
//    /**
//     * 내보낸 파일 공유하기
//     */
//    fun shareBookmarkFile(file: File) {
//        val uri = FileProvider.getUriForFile(
//            context,
//            "${context.packageName}.fileprovider",
//            file
//        )
//
//        val intent = Intent(Intent.ACTION_SEND).apply {
//            type = "text/html"
//            putExtra(Intent.EXTRA_STREAM, uri)
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//
//        context.startActivity(Intent.createChooser(intent, "북마크 공유하기"))
//    }
}