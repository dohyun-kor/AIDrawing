package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.Friend
import com.example.gametset.room.data.model.dto.FriendRequest
import com.example.gametset.room.data.model.response.FriendResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendService {
    // 친구 목록 전체 조회 (모든 상태의 친구 관계 포함)
    @GET("friend/{userId}")
    suspend fun friendList(@Path("userId") userId: Int): MutableList<Friend>

    // 친구 요청
    @POST("friend")
    suspend fun friendRequest(@Body friendRequest: FriendRequest): Boolean

    // 친구 수락 및 거절
    @PUT("friend")
    suspend fun friendAccept(@Body friend: Friend): Boolean

    // 친구 요청 취소
    @DELETE("friend/{friendId}")
    suspend fun cancelFriendRequest(@Path("friendId") friendId: Int): Boolean
}