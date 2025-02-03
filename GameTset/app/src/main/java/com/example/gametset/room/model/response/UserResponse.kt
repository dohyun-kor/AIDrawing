package com.example.gametset.room.model.response

import com.example.gametset.room.model.dto.UserDto

data class UserResponse(val user: UserDto){
    constructor() :this(UserDto())
}