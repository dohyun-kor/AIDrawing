package com.example.gametset.room.data.remote

import com.example.gametset.room.model.dto.UserDto
import com.example.gametset.room.model.response.IsUsedResponse
import com.example.gametset.room.model.response.LoginResponse
import com.example.gametset.room.model.response.UserIdResponse
import com.example.gametset.room.model.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    // 사용자 정보를 추가한다.
    @POST("user/signup")
    suspend fun insert(@Body user:UserDto): Boolean

    // userId로 유저의 정보를 조회한다.
    @GET("user/info")
    suspend fun getUserInfo(@Query("userId") userId:Int): UserResponse

    // 사용자의 아이디로 UserId를 가져온다.
    @GET("user/{userId}")
    suspend fun getUserUserId(@Query("id") id:String): UserIdResponse

    // UserId 중복 조회
    @GET("user/isUsed")
    suspend fun isUsedId(@Query("nickname") id: String): IsUsedResponse

    // nickname 중복 조회
    @GET("user/nickname/isUsed")
    suspend fun isUsedNickname(@Query("id") id: String): IsUsedResponse

    // 로그인 처리 후 성공적으로 로그인 되었다면 loginId라는 쿠키를 내려준다.
    @POST("user/login")
    suspend fun login(@Path("id") id:String, @Query("password") password:String): LoginResponse
}