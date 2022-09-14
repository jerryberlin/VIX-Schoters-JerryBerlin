package com.example.vix_schoters_jerry_berlin.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.vix_schoters_jerry_berlin.data.database.entities.BookmarksEntity
import com.example.vix_schoters_jerry_berlin.data.database.entities.NewsEntity

@Database(
    entities = [NewsEntity::class, BookmarksEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(NewsTypeConverter::class)
abstract class NewsDatabase: RoomDatabase() {

    abstract fun newsDao(): NewsDao

}