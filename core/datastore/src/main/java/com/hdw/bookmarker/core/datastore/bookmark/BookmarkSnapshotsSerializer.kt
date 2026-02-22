package com.hdw.bookmarker.core.datastore.bookmark

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.hdw.bookmarker.core.datastore.proto.BookmarkSnapshotsProto
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalSerializationApi::class)
internal object BookmarkSnapshotsSerializer : Serializer<BookmarkSnapshotsProto> {
    override val defaultValue: BookmarkSnapshotsProto = BookmarkSnapshotsProto()

    override suspend fun readFrom(input: InputStream): BookmarkSnapshotsProto {
        try {
            return ProtoBuf.decodeFromByteArray(
                deserializer = BookmarkSnapshotsProto.serializer(),
                bytes = input.readBytes(),
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Cannot read bookmark snapshots proto.", exception)
        }
    }

    override suspend fun writeTo(t: BookmarkSnapshotsProto, output: OutputStream) {
        output.write(
            ProtoBuf.encodeToByteArray(
                serializer = BookmarkSnapshotsProto.serializer(),
                value = t,
            ),
        )
    }
}
