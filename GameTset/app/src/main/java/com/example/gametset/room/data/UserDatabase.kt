package com.example.gametset.room.data
import com.example.gametset.room.model.dto.UserDto


object UserDatabase {
    private val users = mutableListOf<UserDto>().apply {
        // 더미 데이터 추가
        add(UserDto(1, "test", "test", "admin", 0, 0, 0, 0, 0))
    }

    fun addUser(user: UserDto): Boolean {
        if (isIdExists(user.id) || isNicknameExists(user.nickname)) return false
        users.add(user)
        return true
    }

    fun isIdExists(id: String): Boolean = users.any { it.id == id }

    fun isNicknameExists(nickname: String): Boolean = users.any { it.nickname == nickname }

    // 테스트용 함수
    fun getAllUsers(): List<UserDto> = users.toList()
}