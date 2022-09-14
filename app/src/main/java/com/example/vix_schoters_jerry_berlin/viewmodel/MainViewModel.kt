package com.example.vix_schoters_jerry_berlin.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.example.vix_schoters_jerry_berlin.data.database.entities.BookmarksEntity
import com.example.vix_schoters_jerry_berlin.data.database.entities.NewsEntity
import com.example.vix_schoters_jerry_berlin.data.repository.Repository
import com.example.vix_schoters_jerry_berlin.model.Result
import com.example.vix_schoters_jerry_berlin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    //ROOM DATABSE
    val readNews: LiveData<List<NewsEntity>> = repository.local.readNews().asLiveData()
    val readBookmark: LiveData<List<BookmarksEntity>> = repository.local.readBookmark().asLiveData()

    private fun insertNews(newsEntity: NewsEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertNews(newsEntity)
        }

    fun bookmarkNews(bookmarksEntity: BookmarksEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.bookmarkNews(bookmarksEntity)
        }

    fun deleteBookmarkNews(bookmarksEntity: BookmarksEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteBookmarkNews(bookmarksEntity)
        }

    fun deleteAllBookmarks() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllBookmarks()
        }


    //RETROFIT
    var newsResponse: MutableLiveData<Resource<Result>> = MutableLiveData()

    fun getNews(queries: Map<String, String>) = viewModelScope.launch {
        getNewsSafeCall(queries)
    }

    private suspend fun getNewsSafeCall(queries: Map<String, String>) {
        newsResponse.value = Resource.Loading()
        if(hasInternetConnection()){
            try {
                val response = repository.remote.getNews(queries)
                newsResponse.value = handleNewsResponse(response)

                val news = newsResponse.value!!.data
                if(news != null){
                    offlineCacheNews(news)
                }
            }catch (e: Exception){
                newsResponse.value = Resource.Error("News not found")
            }
        }else{
            newsResponse.value = Resource.Error("No Internet Connection")
        }
    }

    private fun offlineCacheNews(news: Result) {
        val newsEntity = NewsEntity(news)
        insertNews(newsEntity)
    }

    private fun handleNewsResponse(response: Response<Result>): Resource<Result>? {
        when {
            response.message().toString().contains("timeout") -> {
                return Resource.Error("Timeout")
            }
            response.code() == 400 -> {
                return Resource.Error("Bad Request")
            }
            response.code() == 429 -> {
                return Resource.Error("API Key Limited")
            }
            response.code() == 500 -> {
                return Resource.Error("Server Error")
            }
            response.body()!!.articles.isEmpty() -> {
                return Resource.Error("News not found")
            }
            response.isSuccessful -> {
                val newsApi = response.body()
                return Resource.Success(newsApi!!)
            }
            else ->
                return Resource.Error(response.message())
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}