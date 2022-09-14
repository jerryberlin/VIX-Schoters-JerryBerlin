package com.example.vix_schoters_jerry_berlin.data.repository

import com.example.vix_schoters_jerry_berlin.data.network.NewsApi
import com.example.vix_schoters_jerry_berlin.model.Result
import retrofit2.Response
import javax.inject.Inject

class NewsApiRepository @Inject constructor(
    private val newsApi: NewsApi
) {
    suspend fun getNews(queries: Map<String, String>): Response<Result> {
        return newsApi.getNews(queries)
    }
}