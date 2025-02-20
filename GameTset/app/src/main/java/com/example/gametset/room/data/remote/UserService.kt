package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.UserDto
import com.example.gametset.room.data.model.dto.UserProfileChangeDto
import com.example.gametset.room.data.model.response.LoginResponse
import com.example.gametset.room.data.model.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    // 사용자 정보를 추가한다.
    @POST("user/signup")
    suspend fun insert(@Body user: UserDto): Boolean

    // userId로 유저의 정보를 조회한다.
    @GET("user/{userId}/info")
    suspend fun getUserInfo(@Path("userId") userId:Int): UserResponse

    // 사용자의 아이디로 UserId를 가져온다.
    @GET("user/{nickname}")
    suspend fun getUserDetailsByNickname(@Path("nickname") id:String): UserResponse

    // UserId 중복 조회
    @GET("user/isUsed")
    suspend fun isUsedId(@Query("id") id: String): Boolean

    // nickname 중복 조회
    @GET("user/nickname/isUsed")
    suspend fun isUsedNickname(@Query("nickname") nickname: String): Boolean

    // 로그인 처리 후 성공적으로 로그인 되었다면 loginId라는 쿠키를 내려준다.
    @POST("user/login")
    suspend fun login(@Body user: UserDto): LoginResponse

    //유저 프로필 변경
    @PATCH("user/{userId}/profile")
    suspend fun userProfileUpdate(@Path("userId") userId:Int, @Body userProfileChangeDto: UserProfileChangeDto) : Boolean

    @PUT("user/nickname/{userId}")
    suspend fun userNicknameUpdate(@Path("userId") userId:Int, @Query("nickname") nickname: String) : Boolean
}