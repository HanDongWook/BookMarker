@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.hdw.bookmarker.core.datastore.proto

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class BookmarkSnapshotsProto(
    @ProtoNumber(1) val schemaVersion: Int = 1,
    @ProtoNumber(2) val snapshots: List<BrowserBookmarkSnapshotProto> = emptyList(),
)

@Serializable
data class BrowserBookmarkSnapshotProto(
    @ProtoNumber(1) val browserPackage: String,
    @ProtoNumber(2) val importedAtEpochMs: Long,
    @ProtoNumber(3) val sourceHash: String = "",
    @ProtoNumber(4) val document: BookmarkDocumentProto,
)

@Serializable
data class BookmarkDocumentProto(
    @ProtoNumber(1) val title: String = "",
    @ProtoNumber(2) val metas: Map<String, String> = emptyMap(),
    @ProtoNumber(3) val rootItems: List<BookmarkNodeProto> = emptyList(),
)

@Serializable
data class BookmarkNodeProto(
    @ProtoNumber(1) val folder: BookmarkFolderProto? = null,
    @ProtoNumber(2) val bookmark: BookmarkLinkProto? = null,
)

@Serializable
data class BookmarkFolderProto(
    @ProtoNumber(1) val title: String,
    @ProtoNumber(2) val addDate: String = "",
    @ProtoNumber(3) val lastModified: String = "",
    @ProtoNumber(4) val children: List<BookmarkNodeProto> = emptyList(),
)

@Serializable
data class BookmarkLinkProto(
    @ProtoNumber(1) val title: String,
    @ProtoNumber(2) val url: String,
    @ProtoNumber(3) val addDate: String = "",
    @ProtoNumber(4) val lastModified: String = "",
    @ProtoNumber(5) val iconUri: String = "",
)
