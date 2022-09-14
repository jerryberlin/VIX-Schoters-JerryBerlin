package com.example.vix_schoters_jerry_berlin.data.repository

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(
    newsApiRepository: NewsApiRepository,
    databaseRepository: DatabaseRepository
) {
    val remote = newsApiRepository
    val local = databaseRepository
}