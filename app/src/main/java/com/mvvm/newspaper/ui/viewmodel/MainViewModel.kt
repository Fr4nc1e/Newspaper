package com.mvvm.newspaper.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvvm.newspaper.model.Article
import com.mvvm.newspaper.model.NewsResponse
import com.mvvm.newspaper.repository.NewsRepository
import com.mvvm.newspaper.util.ResponseConvertor
import com.mvvm.newspaper.util.Result
import kotlinx.coroutines.launch

class MainViewModel(
    private val newsRepository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Result<NewsResponse>> = MutableLiveData()

    private var breakingNewsPage = 1

    val searchNews: MutableLiveData<Result<NewsResponse>> = MutableLiveData()

    private var searchNewsPage = 1

    init {
        getBreakingNews("us")
    }

    @Suppress("SameParameterValue")
    private fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Result.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(ResponseConvertor.breakingNewsResponseToResult(response))
    }

    fun searchNews(countryCode: String) = viewModelScope.launch {
        searchNews.postValue(Result.Loading())
        val response = newsRepository.searchNews(countryCode, searchNewsPage)
        searchNews.postValue(ResponseConvertor.searchNewsResponseToResult(response))
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }
}
