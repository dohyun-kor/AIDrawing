package com.example.gametset.room.data.remote

import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.MyItemDto

class RetrofitUtil {
    companion object {
        val userService = ApplicationClass.retrofit.create(UserService::class.java)
        val storeService = ApplicationClass.retrofit.create(StoreService::class.java)
        val MyItemService = ApplicationClass.retrofit.create(MyItemService::class.java)

    }
}