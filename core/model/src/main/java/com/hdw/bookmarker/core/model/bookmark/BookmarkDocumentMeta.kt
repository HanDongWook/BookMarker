package com.hdw.bookmarker.core.model.bookmark

const val BOOKMARK_DOCUMENT_META_KIND = "kind"
const val BOOKMARK_DOCUMENT_KIND_INBOX = "inbox"

fun BookmarkDocument.isInboxSnapshot(): Boolean =
    metas[BOOKMARK_DOCUMENT_META_KIND] == BOOKMARK_DOCUMENT_KIND_INBOX
