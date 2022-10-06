package com.silverorange.videoplayer

import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("/videos")
    suspend fun getVideos(): Response<List<VideoModel>>
}