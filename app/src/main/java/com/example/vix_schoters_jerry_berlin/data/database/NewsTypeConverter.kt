package com.example.vix_schoters_jerry_berlin.data.database

import androidx.room.TypeConverter
import com.example.vix_schoters_jerry_berlin.model.Article
import com.example.vix_schoters_jerry_berlin.model.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NewsTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun newsToString(result: Result): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToNews(data: String): Result {
        val listType = object : TypeToken<Result>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun articleToString(article: Article): String {
        return gson.toJson(article)
    }

    @TypeConverter
    fun stringToArticle(data: String): Article {
        val listType = object : TypeToken<Article>() {}.type
        return gson.fromJson(data, listType)
    }

}