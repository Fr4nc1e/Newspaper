package com.mvvm.newspaper.repository

import com.mvvm.newspaper.api.RetrofitInstance
import com.mvvm.newspaper.database.db.ArticleDataBase
import com.mvvm.newspaper.model.Article

class NewsRepository(
    private val dataBase: ArticleDataBase
) {
    suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(
        searchQuery: String,
        pageNumber: Int
    ) = RetrofitInstance.api.searchNews(searchQuery, pageNumber)

    suspend fun upsert(
        article: Article
    ) = dataBase.getArticleDao().upsert(article)

    suspend fun deleteArticle(
        article: Article
    ) = dataBase.getArticleDao().deleteArticle(article)

    fun getSavedNews() = dataBase.getArticleDao().getAllArticles()
}
