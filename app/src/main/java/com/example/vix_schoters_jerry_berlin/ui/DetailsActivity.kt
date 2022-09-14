package com.example.vix_schoters_jerry_berlin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import coil.load
import com.example.vix_schoters_jerry_berlin.R
import com.example.vix_schoters_jerry_berlin.data.database.entities.BookmarksEntity
import com.example.vix_schoters_jerry_berlin.databinding.ActivityDetailsBinding
import com.example.vix_schoters_jerry_berlin.model.Article
import com.example.vix_schoters_jerry_berlin.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityDetailsBinding

    private var newsSaved = false
    private var savedNewsId = 0

    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val args: Article? = args.article

        if (args?.urlToImage == null || !args?.urlToImage.contains("https"))
            binding.imageView.load(R.drawable.baseline_image_24)
        else
            binding.imageView.load(args?.urlToImage)

        binding.titleTextView.text = args?.title

        binding.authorSourceTextView.text = args?.author + "-" + args?.source?.name

        binding.publishedTextView.text = args?.publishedAt?.split("T", "Z").toString()

        binding.descriptionTextView.text = args?.description


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        menuItem = menu!!.findItem(R.id.save_to_bookmarks_menu)
        checkSavedNews(menuItem)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.save_to_bookmarks_menu && !newsSaved) {
            saveToFavorites(item)
        } else if (item.itemId == R.id.save_to_bookmarks_menu && newsSaved){
            deleteBookmarkNews(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkSavedNews(menuItem: MenuItem) {
        mainViewModel.readBookmark.observe(this) { bookmarksEntity ->
            try {
                for (savedNews in bookmarksEntity) {
                    if (savedNews.article.title == args.article.title) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedNewsId = savedNews.id
                        newsSaved = true
                    }
                }
            } catch (e: Exception) {
                Log.d("TAG", "checkSavedNews: ${e.message.toString()}")
            }
        }
    }

    private fun saveToFavorites(item: MenuItem) {
        val bookmarksEntity =
            BookmarksEntity(
                0,
                args.article
            )
        mainViewModel.bookmarkNews(bookmarksEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackbar("News saved")
        newsSaved = true
    }

    private fun deleteBookmarkNews(item: MenuItem){
        val bookmarksEntity =
            BookmarksEntity(
                savedNewsId,
                args.article
            )
        mainViewModel.deleteBookmarkNews(bookmarksEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackbar("Removed from Bookmarks")
        newsSaved = false
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.detailsActivity,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}.show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon?.setTint(ContextCompat.getColor(this, color))
    }

    override fun onDestroy() {
        super.onDestroy()
        changeMenuItemColor(menuItem, R.color.white)
    }
}