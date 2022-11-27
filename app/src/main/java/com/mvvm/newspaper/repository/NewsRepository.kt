package com.mvvm.newspaper.repository

import com.mvvm.newspaper.api.RetrofitInstance
import com.mvvm.newspaper.database.db.ArticleDataBase

class NewsRepository(
    val dataBase: ArticleDataBase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
}
