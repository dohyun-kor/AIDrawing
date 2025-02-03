package com.example.gametset.room.data.remote

import com.example.gametset.room.base.ApplicationClass

class RetrofitUtil {
    companion object {
        val userService = ApplicationClass.retrofit.create(UserService::class.java)

    }
}