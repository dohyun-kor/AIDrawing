package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.Friend
import com.example.gametset.room.data.model.dto.FriendRequest
import com.example.gametset.room.data.model.response.FriendResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendService {
    // 친구 목록 전체 조회
    @GET("friend/list")
    suspend fun friendList(@Query("userId") userId: Int): List<FriendResponse>

    // 친구 요청
    @POST("friend/request")
    suspend fun friendRequest(
        @Body friendRequest: FriendRequest,
    ): Boolean

    // 친구 수락 및 거절
    @PUT("friend/request")
    suspend fun friendAccept(
        @Body frend: Friend,
    ): Boolean

}