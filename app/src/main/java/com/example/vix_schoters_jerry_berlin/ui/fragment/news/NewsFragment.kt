package com.example.vix_schoters_jerry_berlin.ui.fragment.news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vix_schoters_jerry_berlin.R
import com.example.vix_schoters_jerry_berlin.viewmodel.MainViewModel
import com.example.vix_schoters_jerry_berlin.adapter.NewsAdapter
import com.example.vix_schoters_jerry_berlin.databinding.FragmentNewsBinding
import com.example.vix_schoters_jerry_berlin.util.Constant.API_KEY
import com.example.vix_schoters_jerry_berlin.util.NetworkListener
import com.example.vix_schoters_jerry_berlin.util.Resource
import com.example.vix_schoters_jerry_berlin.util.observeOnce
import com.example.vix_schoters_jerry_berlin.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private val args by navArgs<NewsFragmentArgs>()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var newsViewModel: NewsViewModel
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val mAdapter by lazy { NewsAdapter() }

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        newsViewModel = ViewModelProvider(requireActivity())[NewsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        setUpRecyclerView()

        newsViewModel.readBackOnline.observe(viewLifecycleOwner){
            newsViewModel.backOnline = it
        }

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect{ status ->
                    Log.d("TAG", "onCreateView: ${status.toString()}")
                    newsViewModel.networkStatus = status
                    newsViewModel.showNetworkStatus()
                    readDatabase()
                }
        }

        binding.newsFab.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_newsBottomSheet)
        }

        return binding.root
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readNews.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("TAG", "readDatabase called")
                    mAdapter.setData(database.first().result)
                }else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData(){
        Log.d("TAG", "requestApiData called")
        mainViewModel.getNews(newsViewModel.applyQueries())
        mainViewModel.newsResponse.observe(viewLifecycleOwner){ response ->
            Log.d("TAG", "requestApiData: $response")
            when(response){
                is Resource.Success -> {
                    response.data?.let {
                        mAdapter.setData(it)
                    }
                }
                is Resource.Error -> {
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    // TODO: Circular Progress
                }
            }
        }
    }

    private fun loadDataFromCache(){
        lifecycleScope.launch {
            mainViewModel.readNews.observe(viewLifecycleOwner) {database ->
                if(database.isNotEmpty()){
                    mAdapter.setData(database.first().result)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}