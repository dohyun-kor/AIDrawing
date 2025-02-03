package com.example.gametset.room.model.dto

data class UserDto(
    val id: String,
    val password: String,
    val nickname: String
){
    constructor() : this("", "", "")
    constructor(id: String, pass: String) : this(id, pass,"")
}