package com.mvvm.newspaper.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.newspaper.R
import com.mvvm.newspaper.databinding.FragmentBreakingNewsBinding
import com.mvvm.newspaper.ui.MainActivity
import com.mvvm.newspaper.ui.adapter.MainAdapter
import com.mvvm.newspaper.ui.viewmodel.MainViewModel
import com.mvvm.newspaper.util.Constants.QUERY_PAGE_SIZE
import com.mvvm.newspaper.util.Constants.TAG_BREAKING
import com.mvvm.newspaper.util.Result

class BreakingNewsFragment : Fragment() {

    private var _binding: FragmentBreakingNewsBinding? = null

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
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(
            viewLifecycleOwner
        ) { response ->
            when (response) {
                is Result.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = MainViewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            binding?.rvBreakingNews?.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Result.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG_BREAKING, "An error occurred: $message")
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
        binding?.rvBreakingNews?.apply {
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
                        val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

                        shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                            isTotalMoreThanVisible && isScrolling

                        if (shouldPaginate) {
                            viewModel.searchNews("us")
                            isScrolling = false
                        }
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
