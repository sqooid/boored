package com.example.boored.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.boored.util.DisplayModel

// java.util.Calendar.getInstance().toString()

@Entity(tableName = "favourites_table")
data class FavouritesDatabaseFormat(
    @PrimaryKey
    val fileUrl: String,
    @ColumnInfo
    val sampleFileUrl: String,
    @ColumnInfo
    val domain: Int,
    @ColumnInfo
    val createdAt: String,
    @ColumnInfo
    val source: String,
    @ColumnInfo
    val tagStringGeneral: String,
    @ColumnInfo
    val tagStringCharacter: String,
    @ColumnInfo
    val tagStringCopyright: String,
    @ColumnInfo
    val tagStringArtist: String,
    @ColumnInfo
    val tagStringMeta: String,
    @ColumnInfo
    val id: Int,
    @ColumnInfo
    val previewFileUrl: String,
    @ColumnInfo
    val dateFavourited: String,
    @ColumnInfo
    val imageWidth: Int,
    @ColumnInfo
    val imageHeight: Int,
    @ColumnInfo
    val fileExt: String,
)

fun List<FavouritesDatabaseFormat>.toDisplayModel(): List<DisplayModel> {
    return map {
        DisplayModel(
            id = it.id,
            createdAt = it.createdAt,
            source = it.source,
            tagStringGeneral = it.tagStringGeneral,
            tagStringArtist = it.tagStringArtist,
            tagStringMeta = it.tagStringMeta,
            tagStringCharacter = it.tagStringCharacter,
            tagStringCopyright = it.tagStringCopyright,
            fileUrl = it.fileUrl,
            previewFileUrl = it.previewFileUrl,
            imageHeight = it.imageHeight,
            imageWidth = it.imageWidth,
            fileExt = it.fileExt,
            sampleFileUrl = it.sampleFileUrl,
            domain = it.domain,
            dateFavourited = it.dateFavourited,
        )
    }
}
