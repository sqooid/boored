package com.example.boored.network

import com.example.boored.util.Constants
import com.example.boored.util.DisplayModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tickaroo.tikxml.annotation.*

/**
 * Gelbooru models
 */
@JsonClass(generateAdapter = true)
data class GelbooruPostNetJson(
    val id: Int,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "file_url") val  fileUrl: String,
    val height: Int,
    val width: Int,
    val tags: String,
    val source: String
)

@Xml(name = "singlePost")
class GelbooruSingleWrapper {
    @Attribute
    var count: String = ""
    @Attribute
    var offset: String = ""
    @Element
    var post: GelbooruPostNet? = null
}

@Xml(name = "posts")
class GelbooruWrapper {
    @Attribute
    var count: String = ""
    @Attribute
    var offset: String = ""
    @Element
    var postList: List<GelbooruPostNet> = mutableListOf()
}

@Xml(name = "post")
data class GelbooruPostNet(
    @Attribute val id: String,
    @Attribute(name = "created_at") val createdAt: String,
    @Attribute(name = "file_url") val  fileUrl: String,
    @Attribute(name = "preview_url") val previewFileUrl: String,
    @Attribute val height: String,
    @Attribute val width: String,
    @Attribute val tags: String,
    @Attribute val source: String,
    @Attribute val score: String,
    @Attribute val parent_id: String,
    @Attribute val sample_url: String,
    @Attribute val sample_width: String,
    @Attribute val sample_height: String,
    @Attribute val rating: String,
    @Attribute val change: String,
    @Attribute val md5: String,
    @Attribute val creator_id: String,
    @Attribute val has_children: String,
    @Attribute val status: String,
    @Attribute val has_notes: String,
    @Attribute val has_comments: String,
    @Attribute val preview_width: String,
    @Attribute val preview_height: String
)

/**
 * Single post conversion function
 */
fun GelbooruPostNet.gelbooruToDisplayModel(): DisplayModel {
    return DisplayModel(
        domain = Constants.GELBOORU,
        fileUrl = this.fileUrl,
        id = this.id.toInt(),
        createdAt = this.createdAt,
        source = this.source,
        tagStringGeneral = this.tags,
        tagStringArtist = "",
        tagStringCharacter = "",
        tagStringCopyright = "",
        tagStringMeta = "",
        previewFileUrl = this.previewFileUrl,
        imageHeight = this.height.toInt(),
        imageWidth = this.width.toInt(),
        fileExt = "[^.]+$".toRegex().find(this.fileUrl)!!.value,
        sampleFileUrl = this.sample_url,
        dateFavourited = "",
    )
}

/**
 * List function to convert net data to display model
 */
fun List<GelbooruPostNet>.gelbooruToDisplayModel(): List<DisplayModel> {
    return map {
        DisplayModel(
            domain = Constants.GELBOORU,
            fileUrl = it.fileUrl,
            id = it.id.toInt(),
            createdAt = it.createdAt,
            source = it.source,
            tagStringGeneral = it.tags,
            tagStringArtist = "",
            tagStringCharacter = "",
            tagStringCopyright = "",
            tagStringMeta = "",
            previewFileUrl = it.previewFileUrl,
            imageHeight = it.height.toInt(),
            imageWidth = it.width.toInt(),
            fileExt = "[^.]+$".toRegex().find(it.fileUrl)!!.value,
            sampleFileUrl = it.sample_url,
            dateFavourited = "",
        )
    }
}

/**
 * Danbooru models
 */
/**
 * Network model for tags
 */
@JsonClass(generateAdapter = true)
data class DanbooruTagNet(
    val name: String,
    @Json(name = "post_count") val postCount: Int,
    val category: Int
)

/**
 * Data class for post from api. Includes all meta-data
 */
@JsonClass(generateAdapter = true)
data class DanbooruPostNet(
    @Json(name = "file_url") val fileUrl: String?,
    val id: Int?,
    val score: Int?,
    val source: String?,
    val rating: String?,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "uploader_id") val uploaderId: String?,
    @Json(name = "fav_count") val favCount: Int?,
    @Json(name = "parent_id") val parentId: Int?,
    @Json(name = "pool_string") val poolString: String?,
    @Json(name = "is_favorited") val isFavorited: Boolean?,
    @Json(name = "tag_string_general") val tagStringGeneral: String,
    @Json(name = "tag_string_character") val tagStringCharacter: String,
    @Json(name = "tag_string_copyright") val tagStringCopyright: String,
    @Json(name = "tag_string_artist") val tagStringArtist: String,
    @Json(name = "tag_string_meta") val tagStringMeta: String,
    @Json(name = "preview_file_url") val previewFileUrl: String?,
    @Json(name = "image_width") val imageWidth: Int,
    @Json(name = "image_height") val imageHeight: Int,
    @Json(name = "file_ext") val fileExt: String?,
    @Json(name = "large_file_url") val sampleFileUrl: String?,
//    val md5: String,
//    @Json(name = "last_comment_bumped_at") val lastCommentBumpedAt: String,
//    @Json(name = "tag_string") val tagString: String,
//    @Json(name = "is_note_locked") val isNoteLocked: Boolean,
//    @Json(name = "last_noted_at") val lastNotedAt: String,
//    @Json(name = "is_rating_locked") val isRatingLocked: Boolean,
//    @Json(name = "has_children") val hasChildren: Boolean,
//    @Json(name = "approver_id") val approverId: String,
//    @Json(name = "tag_count_general") val tagCountGeneral: Int,
//    @Json(name = "tag_count_artist") val tagCountArtist: Int,
//    @Json(name = "tag_count_character") val tagCountCharacter: Int,
//    @Json(name = "tag_count_copyright") val tagCountCopyright: Int,
//    @Json(name = "file_size") val fileSize: Int,
//    @Json(name = "is_status_locked") val isStatusLocked: Boolean,
//    @Json(name = "up_score") val upScore: Int,
//    @Json(name = "down_score") val downScore: Int,
//    @Json(name = "is_pending") val isPending: Boolean,
//    @Json(name = "is_flagged") val isFlagged: Boolean,
//    @Json(name = "is_deleted") val isDeleted: Boolean,
//    @Json(name = "tag_count") val tagCount: Int,
//    @Json(name = "updated_at") val updateAt: String,
//    @Json(name = "is_banned") val isBanned: Boolean,
//    @Json(name = "pixiv_id") val pixivId: Int,
//    @Json(name = "last_commented_at") val lastCommentedAt: String,
//    @Json(name = "has_active_children") val hasActiveChildren: Boolean,
//    @Json(name = "bit_flags") val bitFlags: Int,
//    @Json(name = "tag_count_meta") val tagCountMeta: Int,
//    @Json(name = "has_large") val hasLarge: Boolean?,
//    @Json(name = "has_visible_children") val hasVisibleChildren: Boolean,
)

/**
 * Extension function for conversion of network model to display model of post
 */
fun DanbooruPostNet.danbooruToDisplayModel(): DisplayModel {
    return DisplayModel(
        domain = Constants.DANBOORU,
        id = this.id ?: -1,
        createdAt = this.createdAt ?: "",
        source = this.source ?: "",
        tagStringGeneral = this.tagStringGeneral,
        tagStringArtist = this.tagStringArtist,
        tagStringMeta = this.tagStringMeta,
        tagStringCharacter = this.tagStringCharacter,
        tagStringCopyright = this.tagStringCopyright,
        fileUrl = this.fileUrl ?: "",
        previewFileUrl = this.previewFileUrl ?: "",
        imageWidth = this.imageWidth,
        imageHeight = this.imageHeight,
        fileExt = this.fileExt ?: "",
        sampleFileUrl = this.sampleFileUrl ?: "",
        dateFavourited = "",
    )
}

/**
 * Extension function for conversion of network model to display model of post
 */
fun List<DanbooruPostNet>.danbooruToDisplayModel(): List<DisplayModel> {
    return map {
        DisplayModel(
            domain = Constants.DANBOORU,
            id = it.id ?: -1,
            createdAt = it.createdAt ?: "",
            source = it.source ?: "",
            tagStringGeneral = it.tagStringGeneral,
            tagStringArtist = it.tagStringArtist,
            tagStringMeta = it.tagStringMeta,
            tagStringCharacter = it.tagStringCharacter,
            tagStringCopyright = it.tagStringCopyright,
            fileUrl = it.fileUrl ?: "",
            previewFileUrl = it.previewFileUrl ?: "",
            imageWidth = it.imageWidth,
            imageHeight = it.imageHeight,
            fileExt = it.fileExt ?: "",
            sampleFileUrl = it.sampleFileUrl ?: "",
            dateFavourited = "",
        )
    }.filter {
        it.id != -1
    }
}

