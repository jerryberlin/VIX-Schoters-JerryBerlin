package com.example.vix_schoters_jerry_berlin.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.vix_schoters_jerry_berlin.model.Article
import com.example.vix_schoters_jerry_berlin.util.Constant.BOOKMARKS_NEWS_TABLE

@Entity(tableName = BOOKMARKS_NEWS_TABLE)
class BookmarksEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var article: Article
)