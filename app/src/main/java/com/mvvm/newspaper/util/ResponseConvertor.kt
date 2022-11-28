package com.mvvm.newspaper.util

import com.mvvm.newspaper.model.NewsResponse
import com.mvvm.newspaper.ui.viewmodel.MainViewModel
import retrofit2.Response

object ResponseConvertor {

    fun breakingNewsResponseToResult(
        response: Response<NewsResponse>
    ): Result<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                MainViewModel.breakingNewsPage++
                if (MainViewModel.breakingNewsResponse == null) {
                    MainViewModel.breakingNewsResponse = it
                } else {
                    val oldArticles = MainViewModel.breakingNewsResponse?.articles
                    val newArticles = it.articles
                    oldArticles?.addAll(newArticles)
                }

                return Result.Success(MainViewModel.breakingNewsResponse ?: it)
            }
        }
        return Result.Error(response.message())
    }

    fun searchNewsResponseToResult(
        response: Response<NewsResponse>
    ): Result<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                MainViewModel.searchNewsPage++
                if (MainViewModel.searchNewsResponse == null) {
                    MainViewModel.searchNewsResponse = it
                } else {
                    val oldArticles = MainViewModel.searchNewsResponse?.articles
                    val newArticles = it.articles
                    oldArticles?.addAll(newArticles)
                }

                return Result.Success(MainViewModel.searchNewsResponse ?: it)
            }
        }
        return Result.Error(response.message())
    }
}
