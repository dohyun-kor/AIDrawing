package com.ssafy.smartstore_jetpack.data.remote

import com.ssafy.smartstore_jetpack.base.ApplicationClass

class RetrofitUtil {
    companion object{
        val commentService = ApplicationClass.retrofit.create(CommentService::class.java)
        val orderService = ApplicationClass.retrofit.create(OrderService::class.java)
        val productService = ApplicationClass.retrofit.create(ProductService::class.java)
        val userService = ApplicationClass.retrofit.create(UserService::class.java)
        val opneAiService = ApplicationClass.retrofit.create(OpenAiService::class.java)
    }
}