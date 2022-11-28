package com.mvvm.newspaper.util

import com.mvvm.newspaper.model.NewsResponse
import retrofit2.Response

object ResponseConvertor {

    fun breakingNewsResponseToResult(
        response: Response<NewsResponse>
    ): Result<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(response.message())
    }

    fun searchNewsResponseToResult(
        response: Response<NewsResponse>
    ): Result<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }
        return Result.Error(response.message())
    }
}
