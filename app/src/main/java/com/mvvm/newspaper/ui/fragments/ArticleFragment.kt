package com.mvvm.newspaper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mvvm.newspaper.databinding.FragmentArticleBinding
import com.mvvm.newspaper.ui.MainActivity
import com.mvvm.newspaper.ui.viewmodel.MainViewModel

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null

    private val binding get() = _binding

    private val args: ArticleFragmentArgs? by navArgs()

    private val viewModel: MainViewModel by lazy {
        (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        val article = args?.article
        binding?.webView?.apply {
            webViewClient = WebViewClient()
            article?.let {
                loadUrl(it.url)
            }
        }

        binding?.fab?.setOnClickListener {
            article?.let { article ->
                viewModel.saveArticle(article)
            }
            view?.let { view ->
                Snackbar.make(
                    view,
                    "Article saved.",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setAction("Undo") {
                        article?.let { article -> viewModel.deleteArticle(article) }
                    }
                    show()
                }
            }
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
