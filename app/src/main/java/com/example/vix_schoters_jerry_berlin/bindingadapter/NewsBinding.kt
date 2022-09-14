package com.example.vix_schoters_jerry_berlin.bindingadapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.example.vix_schoters_jerry_berlin.data.database.entities.NewsEntity
import com.example.vix_schoters_jerry_berlin.model.Result
import com.example.vix_schoters_jerry_berlin.util.Resource

class NewsBinding {
    companion object {

        @BindingAdapter("readApiResponse","readDatabase",requireAll = true)
        @JvmStatic
        fun handleReadDataErrors(
            view: View,
            apiResponse: Resource<Result>?,
            database: List<NewsEntity>?
        ){
            when (view){
                is ImageView ->{
                    view.isVisible = apiResponse is Resource.Error && database.isNullOrEmpty()
                }
                is TextView ->{
                    view.isVisible = apiResponse is Resource.Error && database.isNullOrEmpty()
                    view.text = apiResponse?.message.toString()
                }
            }
        }
    }
}