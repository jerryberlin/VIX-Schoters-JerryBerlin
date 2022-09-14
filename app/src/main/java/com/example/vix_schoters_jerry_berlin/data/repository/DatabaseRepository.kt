package com.example.vix_schoters_jerry_berlin.data.repository

import com.example.vix_schoters_jerry_berlin.data.database.NewsDao
import com.example.vix_schoters_jerry_berlin.data.database.entities.BookmarksEntity
import com.example.vix_schoters_jerry_berlin.data.database.entities.NewsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val newsDao: NewsDao
) {

    fun readNews(): Flow<List<NewsEntity>> {
        return newsDao.readNews()
    }

    suspend fun insertNews(newsEntity: NewsEntity) {
        newsDao.insertNews(newsEntity)
    }

    suspend fun bookmarkNews(bookmarksEntity: BookmarksEntity) {
        newsDao.bookmarkNews(bookmarksEntity)
    }

    fun readBookmark(): Flow<List<BookmarksEntity>> {
        return newsDao.readBookmark()
    }

    suspend fun deleteBookmarkNews(bookmarksEntity: BookmarksEntity) {
        newsDao.deleteBookmarkNews(bookmarksEntity)
    }

    suspend fun deleteAllBookmarks() {
        newsDao.deleteAllBookmarks()
    }



}