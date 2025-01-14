package com.ssafy.smartstore_jetpack.data.model.response

import com.ssafy.smartstore_jetpack.data.model.dto.Grade
import com.ssafy.smartstore_jetpack.data.model.dto.Order
import com.ssafy.smartstore_jetpack.data.model.dto.User

data class UserResponse(val grade: Grade, val user: User, var order:List<Order>){
    constructor() : this(
        grade = Grade("", "", 0, 0,0), // Grade에 기본값 설정
        user = User("defaultUserName", "defaultUserEmail"), // User에 기본값 설정
        order = emptyList() // Order 리스트는 빈 리스트로 초기화
    )
}