package com.example.gametset.room.data.remote

import com.example.gametset.room.data.model.dto.OneRoomDto
import com.example.gametset.room.data.model.dto.RoomChangeDto
import com.example.gametset.room.data.model.dto.RoomCreateDto
import com.example.gametset.room.data.model.dto.RoomDto
import com.example.gametset.room.data.model.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoomService {

    @GET("room/list")
    suspend fun getAllRoom(): List<OneRoomDto>

    @POST("room")
    suspend fun createRoom(@Body RoomCreateDto: RoomCreateDto): Int

    @PUT("room/{roomId}")
    suspend fun changeRoomInfo(@Path("roomId") roomId:Int, @Body roomChangeDto : RoomChangeDto) : Boolean

    @GET("room/{roomId}")
    suspend fun getOneRoom(@Path("roomId") roomId: Int): OneRoomDto
}
