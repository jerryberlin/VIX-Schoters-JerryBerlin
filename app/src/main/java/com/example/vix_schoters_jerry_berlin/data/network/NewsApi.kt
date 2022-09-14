package com.example.vix_schoters_jerry_berlin.data.network

import com.example.vix_schoters_jerry_berlin.model.Result
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface NewsApi {

    @GET("/v2/top-headlines")
    suspend fun getNews(
        @QueryMap queries: Map<String, String>
    ): Response<Result>
}