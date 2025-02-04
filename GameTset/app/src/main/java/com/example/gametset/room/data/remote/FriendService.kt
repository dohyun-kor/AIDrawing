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

interface FriendService {
    // 사용자 정보를 추가한다.
    @POST("/friend/list")
    suspend fun friendList(@Query("userId") userId:Int): Boolean
}