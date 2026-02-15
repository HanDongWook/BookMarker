package com.hdw.bookmarker.core.data.bookmarker.chrome

class BookmarkParser {

//    /**
//     * Chrome 북마크 HTML 파일 파싱
//     */
//    fun parseBookmarkHtml(inputStream: InputStream): List<Bookmark> {
//        val bookmarks = mutableListOf<Bookmark>()
//
//        try {
//            val html = inputStream.bufferedReader().use { it.readText() }
//            val document: Document = Jsoup.parse(html)
//
//            // <A> 태그 찾기 (북마크 링크)
//            val links = document.select("a")
//
//            links.forEach { link ->
//                val url = link.attr("href")
//                val title = link.text()
//                val addDate = link.attr("add_date").toLongOrNull() ?: 0L
//
//                if (url.isNotEmpty() && title.isNotEmpty()) {
//                    bookmarks.add(Bookmark(title, url, addDate))
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return bookmarks
//    }
//
//    /**
//     * 파일에서 직접 파싱
//     */
//    fun parseBookmarkFile(file: File): List<Bookmark> {
//        return file.inputStream().use { parseBookmarkHtml(it) }
//    }
//
//    /**
//     * 폴더 구조를 포함한 상세 파싱
//     */
//    fun parseBookmarkWithFolders(inputStream: InputStream): BookmarkFolder {
//        val rootFolder = BookmarkFolder("북마크")
//
//        try {
//            val html = inputStream.bufferedReader().use { it.readText() }
//            val document: Document = Jsoup.parse(html)
//
//            // DL 태그로 폴더 구조 파싱
//            val rootDl = document.select("dl").first()
//            rootDl?.let { parseFolderRecursive(it, rootFolder) }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return rootFolder
//    }
//
//    private fun parseFolderRecursive(element: Element, folder: BookmarkFolder) {
//        element.children().forEach { child ->
//            when (child.tagName().lowercase()) {
//                "dt" -> {
//                    val h3 = child.selectFirst("h3")
//                    val a = child.selectFirst("a")
//                    val dl = child.selectFirst("dl")
//
//                    when {
//                        // 폴더
//                        h3 != null && dl != null -> {
//                            val subFolder = BookmarkFolder(h3.text())
//                            parseFolderRecursive(dl, subFolder)
//                            folder.subfolders.add(subFolder)
//                        }
//                        // 북마크
//                        a != null -> {
//                            val url = a.attr("href")
//                            val title = a.text()
//                            val addDate = a.attr("add_date").toLongOrNull() ?: 0L
//
//                            if (url.isNotEmpty()) {
//                                folder.bookmarks.add(Bookmark(title, url, addDate, folder.name))
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}