package com.example.boored.util

import android.os.Parcelable
import com.example.boored.database.FavouritesDatabaseFormat
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DisplayModel(
    val domain: Int,
    val fileUrl: String,
    val id: Int,
    val createdAt: String,
    val source: String,
    val tagStringGeneral: String,
    val tagStringCharacter: String,
    val tagStringCopyright: String,
    val tagStringArtist: String,
    val tagStringMeta: String,
    val previewFileUrl: String,
    val sampleFileUrl: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val fileExt: String,
    val dateFavourited: String,
) : Parcelable

fun DisplayModel.toFavouritesDatabaseFormat(date: String): FavouritesDatabaseFormat {
    return FavouritesDatabaseFormat(
        domain = this.domain,
        sampleFileUrl = this.sampleFileUrl ?: "",
        id = this.id,
        createdAt = this.createdAt,
        source = this.source,
        tagStringGeneral = this.tagStringGeneral,
        tagStringArtist = this.tagStringArtist,
        tagStringMeta = this.tagStringMeta,
        tagStringCharacter = this.tagStringCharacter,
        tagStringCopyright = this.tagStringCopyright,
        fileUrl = this.fileUrl  ?: "",
        previewFileUrl = this.previewFileUrl,
        imageWidth = this.imageWidth,
        imageHeight = this.imageHeight,
        fileExt = this.fileExt,
        dateFavourited = date,
    )
}