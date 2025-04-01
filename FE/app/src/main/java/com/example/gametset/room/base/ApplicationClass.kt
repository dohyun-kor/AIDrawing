package com.example.gametset.room.base

import android.Manifest
import android.app.Application
import android.util.Log
import com.example.gametset.room.data.local.SharedPreferencesUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass: Application() {
    companion object{
        const val SERVER_URL = "https://i12d108.p.ssafy.io/api/"
//        const val SERVER_URL = "http://192.168.100.203:9987/api/"

        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit

        // 모든 퍼미션 관련 배열
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    override fun onCreate() {
        super.onCreate()

        //shared preference 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

        // 레트로핏 인스턴스를 생성하고, 레트로핏에 각종 설정값들을 지정해줍니다.
        // 연결 타임아웃시간은 5초로 지정이 되어있고, HttpLoggingInterceptor를 붙여서 어떤 요청이 나가고 들어오는지를 보여줍니다.
        Log.d("Retrofit_Request_Method3", "intercept3: ${ApplicationClass.sharedPreferencesUtil.getUser().token}")
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .followRedirects(false)  // 리다이렉트 비활성화
            .followSslRedirects(false)  // SSL 리다이렉트 비활성화
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                
                // 302 응답을 성공으로 처리
                if (response.code == 302) {
                    return@addInterceptor response.newBuilder()
                        .code(200)
                        .build()
                }
                response
            }
            .addInterceptor(AuthInterceptor(sharedPreferencesUtil))
            .addInterceptor(AddCookiesInterceptor())
            .addInterceptor(LoggingInterceptor())
            .addInterceptor(ReceivedCookiesInterceptor())
            .build()
        Log.d("Retrofit_Request_Method3", "intercept4: ${ApplicationClass.sharedPreferencesUtil.getUser().token}")


        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }
    //GSon은 엄격한 json type을 요구하는데, 느슨하게 하기 위한 설정. success, fail이 json이 아니라 단순 문자열로 리턴될 경우 처리..
    val gson : Gson = GsonBuilder()
        .setLenient()
        .create()
}