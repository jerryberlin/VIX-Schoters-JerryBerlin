package com.example.vix_schoters_jerry_berlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vix_schoters_jerry_berlin.databinding.NewsRowLayoutBinding
import com.example.vix_schoters_jerry_berlin.model.Article
import com.example.vix_schoters_jerry_berlin.model.Result
import com.example.vix_schoters_jerry_berlin.util.NewsDiffUtil

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {

    private var news = emptyList<Article>()

    class MyViewHolder(private val binding: NewsRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(article: Article){
                binding.article = article

                //Update layout jika ada perubahan dalam data
                binding.executePendingBindings()
            }

            companion object {
                fun from(parent: ViewGroup): MyViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = NewsRowLayoutBinding.inflate(layoutInflater, parent, false)
                    return MyViewHolder(binding)
                }
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return news.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentNews = news[position]
        holder.bind(currentNews)
    }

    fun setData(newData: Result) {
        val newsDiffUtil = NewsDiffUtil(news, newData.articles)
        val diffUtilResult = DiffUtil.calculateDiff(newsDiffUtil)
        news = newData.articles
        diffUtilResult.dispatchUpdatesTo(this)
    }
}