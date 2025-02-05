package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.UserDto
import com.example.gametset.room.data.model.response.FriendResponse
import com.example.gametset.room.data.model.response.IsUsedResponse
import com.example.gametset.room.data.model.response.LoginResponse
import com.example.gametset.room.data.model.response.UserIdResponse
import com.example.gametset.room.data.model.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendService {
    // 사용자 정보를 추가한다.
    @GET("friend/list")
    suspend fun friendList(@Query("userId") userId: Int): List<FriendResponse>

    @POST("friend/request")
    suspend fun friendRequest(
        @Query("userId") userId: Int,
        @Query("receiverId") receiverId: Int
    ): Boolean

    @PUT("friend/request")
    suspend fun friendAccept(
        @Query("userId") userId: Int,
        @Query("friendId") friendId: Int,
        @Query("status") status: Int
    ): Boolean

}