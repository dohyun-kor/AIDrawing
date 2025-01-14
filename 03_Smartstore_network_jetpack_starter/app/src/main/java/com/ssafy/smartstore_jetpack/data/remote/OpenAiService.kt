package com.ssafy.smartstore_jetpack.data.remote

import com.ssafy.smartstore_jetpack.data.model.response.OpenAiResponse
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiService {

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun getFortune(
        @Header("Authorization") authHeader: String,
        @Body requestBody: RequestBody
    ): Call<OpenAiResponse>

}