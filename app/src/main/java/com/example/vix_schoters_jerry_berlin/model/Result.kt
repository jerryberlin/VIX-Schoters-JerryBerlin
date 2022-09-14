package com.example.vix_schoters_jerry_berlin.model


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("articles")
    val articles: List<Article>
)