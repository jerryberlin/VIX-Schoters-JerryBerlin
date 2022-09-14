package com.example.vix_schoters_jerry_berlin.adapter

import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vix_schoters_jerry_berlin.R
import com.example.vix_schoters_jerry_berlin.data.database.entities.BookmarksEntity
import com.example.vix_schoters_jerry_berlin.databinding.BookmarksRowLayoutBinding
import com.example.vix_schoters_jerry_berlin.ui.fragment.bookmark.BookmarksFragmentDirections
import com.example.vix_schoters_jerry_berlin.util.NewsDiffUtil
import com.example.vix_schoters_jerry_berlin.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class BookmarksAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<BookmarksAdapter.MyViewHolder>(), ActionMode.Callback {

    private var multiSelection = false
    private lateinit var rootView: View

    private lateinit var mActionMode: ActionMode

    private var selectedNews = arrayListOf<BookmarksEntity>()
    private var myViewHolder = arrayListOf<MyViewHolder>()
    private var bookmarksNews = emptyList<BookmarksEntity>()

    class MyViewHolder(val binding: BookmarksRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bookmarksEntity: BookmarksEntity) {
            binding.bookmarksEntity = bookmarksEntity

            //Update layout jika ada perubahan dalam data
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BookmarksRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return bookmarksNews.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolder.add(holder)
        rootView = holder.itemView.rootView

        val currentNews = bookmarksNews[position]
        holder.bind(currentNews)

        holder.binding.bookmarksRowLayout.setOnClickListener {
            if (multiSelection) {
                applySelection(holder, currentNews)
            } else {
                val action =
                    BookmarksFragmentDirections.actionBookmarksFragmentToDetailsActivity2(
                        currentNews.article
                    )
                holder.itemView.findNavController().navigate(action)
            }
        }

        holder.binding.bookmarksRowLayout.setOnLongClickListener {
            if (!multiSelection) {
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, currentNews)
                true
            } else {
                applySelection(holder, currentNews)
                true
            }

        }
    }

    private fun applySelection(holder: MyViewHolder, currentNews: BookmarksEntity) {
        if (selectedNews.contains(currentNews)) {
            selectedNews.remove(currentNews)
            changeNewsStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        } else {
            selectedNews.add(currentNews)
            changeNewsStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    private fun applyActionModeTitle() {
        when (selectedNews.size) {
            0 -> {
                mActionMode.finish()
                multiSelection = false
            }
            1 -> {
                mActionMode.title = "${selectedNews.size} item selected"
            }
            else -> {
                mActionMode.title = "${selectedNews.size} items selected"
            }
        }
    }

    private fun changeNewsStyle(holder: MyViewHolder, backgroundColor: Int, strokeColor: Int) {
        holder.binding.bookmarksRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.binding.bookmarksRowCardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.bookmarks_contextual_menu, menu)
        mActionMode = actionMode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if (menu?.itemId == R.id.deleteBookmarkNews) {
            selectedNews.forEach {
                mainViewModel.deleteBookmarkNews(it)
            }
            showSnackbar("${selectedNews.size} News removed.")

            multiSelection = false
            selectedNews.clear()
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        myViewHolder.forEach { holder ->
            changeNewsStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        multiSelection = false
        selectedNews.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor =
            ContextCompat.getColor(requireActivity, color)
    }

    fun setData(newBookmarksNews: List<BookmarksEntity>) {
        val bookmarksDiffUtil =
            NewsDiffUtil(bookmarksNews, newBookmarksNews)
        val diffUtilResult = DiffUtil.calculateDiff(bookmarksDiffUtil)
        bookmarksNews = newBookmarksNews
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    fun clearContextualActionMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }
}