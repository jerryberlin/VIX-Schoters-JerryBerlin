package com.example.vix_schoters_jerry_berlin.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.vix_schoters_jerry_berlin.data.DataStoreRepository
import com.example.vix_schoters_jerry_berlin.util.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
): AndroidViewModel(application) {

    private var country = Constant.DEFAULT_COUNTRY
    private var category = Constant.DEFAULT_CATEGORY

    var networkStatus = false
    var backOnline = false


    val readCountryAndCategoryType = dataStoreRepository.readCountryAndCategoryType
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    fun saveCountryAndCategoryType(country: String, countryId: Int, category: String, categoryId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveCountryAndCategoryType(
                country, countryId, category, categoryId
            )
        }

    fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun applyQueries(): HashMap<String, String>{
        val queries: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readCountryAndCategoryType.collect {value ->
                country = value.selectedCountry
                category = value.selectedCategory
            }
        }

        queries[Constant.QUERY_COUNTRY] = country
        queries[Constant.QUERY_API_KEY] = Constant.API_KEY
        queries[Constant.QUERY_CATEGORY] = category

        Log.d("TEST123", "applyQueries: $queries")
        return queries
    }

    fun showNetworkStatus(){
        if(!networkStatus){
            Toast.makeText(getApplication(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if(networkStatus){
            if(backOnline){
                Toast.makeText(getApplication(), "Internet Connection Available", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }
}