package com.example.gametset.room.base

import android.util.Log
import com.example.gametset.room.data.local.SharedPreferencesUtil
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sharedPreferencesUtil: SharedPreferencesUtil) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 요청에 Authorization 헤더 추가
        val token = sharedPreferencesUtil.getUser().token
        Log.d("Retrofit_AuthInterceptor", "Token: $token")  // 로그로 token 확인

        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}
