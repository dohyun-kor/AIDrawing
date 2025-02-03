package com.example.gametset.room.data
import com.example.gametset.room.model.dto.UserDto


object UserDatabase {
    private val users = mutableListOf<UserDto>().apply {
        // 더미 데이터 추가
        add(UserDto("test1", "Test1!", "asd2"))
        add(UserDto("hong123", "Hong123!", "홍길동"))
        add(UserDto("kim456", "Kim456@", "김철수"))
        add(UserDto("park789", "Park789#", "박영희"))
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