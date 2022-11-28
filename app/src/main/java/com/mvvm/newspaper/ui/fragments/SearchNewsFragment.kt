package com.mvvm.newspaper.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.newspaper.R
import com.mvvm.newspaper.databinding.FragmentSearchNewsBinding
import com.mvvm.newspaper.ui.MainActivity
import com.mvvm.newspaper.ui.adapter.MainAdapter
import com.mvvm.newspaper.ui.viewmodel.MainViewModel
import com.mvvm.newspaper.util.Constants
import com.mvvm.newspaper.util.Constants.DELAY
import com.mvvm.newspaper.util.Constants.TAG_SEARCH
import com.mvvm.newspaper.util.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {

    private var _binding: FragmentSearchNewsBinding? = null

    private val binding get() = _binding

    private val viewModel: MainViewModel by lazy {
        (activity as MainActivity).viewModel
    }

    private lateinit var newsAdapter: MainAdapter

    private var isLoading: Boolean = false

    private var isLastPage: Boolean = false

    private var isScrolling: Boolean = false

    private var shouldPaginate: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job? = null
        binding?.etSearch?.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(DELAY)
                binding?.etSearch?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewModel.searchNews.observe(
            viewLifecycleOwner
        ) { response ->
            when (response) {
                is Result.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = MainViewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            binding?.rvSearchNews?.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Result.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG_SEARCH, "An error occurred: $message")
                    }
                }

                is Result.Loading -> {
                    showProgressBar()
                }
            }
        }

        return binding?.root
    }

    private fun hideProgressBar() {
        binding?.paginationProgressBar?.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding?.paginationProgressBar?.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setUpRecyclerView() {
        newsAdapter = MainAdapter()
        binding?.rvSearchNews?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            isScrolling = true
                        }
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount

                        val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
                        val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                        val isNotAtBeginning = firstVisibleItemPosition >= 0
                        val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE

                        shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                            isTotalMoreThanVisible && isScrolling

                        if (shouldPaginate) {
                            viewModel.searchNews(binding?.etSearch?.text.toString())
                            isScrolling = false
                        }
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
