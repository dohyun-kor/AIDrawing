package com.example.gametset.room.base

import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // 요청 URL 로그 출력
        Log.d("Retrofit_Request_URL", "Request URL: ${request.url}")

        // 요청 메소드 및 헤더 정보 로그 출력 (필요한 경우)
        Log.d("Retrofit_Request_Method", "Request Method: ${request.method}")
        Log.d("Retrofit_Request_Method2", "intercept: ${ApplicationClass.sharedPreferencesUtil.getUser().token}")
        request.headers.forEach { header ->
            Log.d("Retrofit_Request_Header", "Header: ${header.first}: ${header.second}")
        }

        // 요청을 실행하고 응답을 받는다.
        val response = chain.proceed(request)

        // 응답 상태 코드 로그 출력 (선택적)
        Log.d("Retrofit_Response_Code", "Response Code: ${response.code}")

        return response
    }
}
