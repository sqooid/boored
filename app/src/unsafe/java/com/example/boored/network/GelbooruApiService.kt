package com.example.boored.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://gelbooru.com"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val tikXml = TikXml.Builder()
    .exceptionOnUnreadXml(false)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(TikXmlConverterFactory.create(tikXml))
    .baseUrl(BASE_URL)
    .build()

interface GelbooruApiService {
    @GET("/index.php?page=dapi&s=post&q=index&json=0")
    suspend fun getPosts(
        @Query("tags", encoded = true) tags: String,
        @Query("limit") limit: Int,
        @Query("pid") page: Int
    ): GelbooruWrapper

    @GET("/index.php?page=dapi&s=post&q=index&json=0")
    suspend fun getSinglePost(
        @Query("id") id: Int
    ): GelbooruSingleWrapper
}

object GelbooruApi {
    val gelbooruService: GelbooruApiService by lazy {
        retrofit.create(GelbooruApiService::class.java)
    }
}