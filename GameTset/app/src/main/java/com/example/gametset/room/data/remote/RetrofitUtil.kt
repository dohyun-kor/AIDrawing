package com.example.gametset.room.data.remote

import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.Friend
import com.example.gametset.room.data.model.dto.MyItemDto
import com.example.gametset.ranking.data.remote.RankingService
import com.example.gametset.ranking.data.remote.TopRankingService

object RetrofitUtil {
    val storeService = ApplicationClass.retrofit.create(StoreService::class.java)
    val myItemService = ApplicationClass.retrofit.create(MyItemService::class.java)
    val userService = ApplicationClass.retrofit.create(UserService::class.java)
    val friendService = ApplicationClass.retrofit.create(FriendService::class.java)
    val RoomService = ApplicationClass.retrofit.create(RoomService::class.java)
    val rankingService = ApplicationClass.retrofit.create(RankingService::class.java)
    val topRankingService = ApplicationClass.retrofit.create(TopRankingService::class.java)
    val paintingAssessmentService_jw = ApplicationClass.retrofit.create(PaintingAssessmentService_jw::class.java)
    val myRoomService: MyRoomService by lazy {
        ApplicationClass.retrofit.create(MyRoomService::class.java)
    }
    val pictureService = ApplicationClass.retrofit.create(PictureService::class.java)
    val pictureUploadService: PictureUploadService_jw by lazy {
        ApplicationClass.retrofit.create(PictureUploadService_jw::class.java)
    }
}