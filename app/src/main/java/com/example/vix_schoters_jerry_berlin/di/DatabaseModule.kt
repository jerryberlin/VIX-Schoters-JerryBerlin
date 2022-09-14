package com.example.vix_schoters_jerry_berlin.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vix_schoters_jerry_berlin.data.database.NewsDatabase
import com.example.vix_schoters_jerry_berlin.util.Constant.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        NewsDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDao(newsDatabase: NewsDatabase) = newsDatabase.newsDao()


}