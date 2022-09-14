package com.example.vix_schoters_jerry_berlin.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vix_schoters_jerry_berlin.data.database.entities.BookmarksEntity
import com.example.vix_schoters_jerry_berlin.data.database.entities.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(newsEntity: NewsEntity)

    @Query("SELECT * FROM news_table ORDER BY id ASC")
    fun readNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bookmarkNews(bookmarksEntity: BookmarksEntity)

    @Query("SELECT * FROM bookmarks_table ORDER BY id ASC")
    fun readBookmark(): Flow<List<BookmarksEntity>>

    @Delete
    suspend fun deleteBookmarkNews(bookmarksEntity: BookmarksEntity)

    @Query("DELETE FROM bookmarks_table")
    suspend fun deleteAllBookmarks()

}