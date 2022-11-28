package com.mvvm.newspaper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mvvm.newspaper.R
import com.mvvm.newspaper.databinding.FragmentSavedNewsBinding
import com.mvvm.newspaper.ui.MainActivity
import com.mvvm.newspaper.ui.adapter.MainAdapter
import com.mvvm.newspaper.ui.viewmodel.MainViewModel

class SavedNewsFragment : Fragment() {

    private var _binding: FragmentSavedNewsBinding? = null

    private val binding get() = _binding

    private val viewModel: MainViewModel by lazy {
        (activity as MainActivity).viewModel
    }

    private lateinit var newsAdapter: MainAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                view?.let { view ->
                    Snackbar.make(view, "News deleted.", Snackbar.LENGTH_LONG)
                        .apply {
                            setAction("Undo") {
                                viewModel.saveArticle(article)
                            }
                            show()
                        }
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding?.rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner) {
            newsAdapter.differ.submitList(it)
        }

        return binding?.root
    }

    private fun setUpRecyclerView() {
        newsAdapter = MainAdapter()
        binding?.rvSavedNews?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
