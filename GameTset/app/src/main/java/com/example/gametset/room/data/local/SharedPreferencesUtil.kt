package com.example.gametset.room.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.gametset.room.data.model.MyRoomItem
import com.example.gametset.room.data.model.dto.UserDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesUtil(context: Context) {
    private val SHARED_PREFERENCES_NAME = "game_preference"
    private val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()

    companion object {
        private const val USER_ID = "userId"
        private const val USER_PASSWORD = "userPassword"
        private const val USER_NAME = "userName"
        private const val USER_EMAIL = "userEmail"
        private const val USER_COOKIE = "userCookie"
        private const val MY_ROOM_ITEMS = "my_room_items"
    }

    val COOKIES_KEY_NAME = "cookies"

    private val gson = Gson()
    private val MAX_NOTIFICATIONS = 50

    //사용자 정보 저장
    fun addUser(user: UserDto) {
        editor.putInt(USER_ID, user.userId)
        editor.putString(USER_EMAIL, user.email)
        editor.putString(USER_NAME, user.id)
        editor.putString(USER_PASSWORD, user.password)
        editor.putString("nickname", user.nickname)
        editor.putInt("point", user.point)
        editor.putInt("gamesWon", user.gamesWon)
        editor.putInt("totalGames", user.totalGames)
        editor.putInt("level", user.level)
        editor.putInt("exp", user.exp)
        editor.putString("token", user.token)
        editor.apply()
    }

    fun getUser(): UserDto {
        val userId = preferences.getInt(USER_ID, 0)
        val id = preferences.getString(USER_NAME, "")
        val nickname = preferences.getString("nickname", "")
        val point = preferences.getInt("point", 0)
        val gamesWon = preferences.getInt("gamesWon", 0)
        val totalGames = preferences.getInt("totalGames", 0)
        val level = preferences.getInt("level", 0)
        val exp = preferences.getInt("exp", 0)
        val userProfileItemId = preferences.getInt("userProfileItemId", 0)
        val token = preferences.getString("token", "")
        
        return if (id != "") {
            UserDto(
                userId,
                "",
                id!!,
                "",
                nickname!!,
                point,
                gamesWon,
                totalGames,
                level,
                exp,
                userProfileItemId,
                token!!
            )
        } else {
            UserDto()
        }
    }

    fun localUpdateNickname(nickname : String){
        editor.putString("nickname",nickname)
        editor.apply()
    }

    fun deleteUser() {
        // 유저 관련 데이터만 삭제
        editor.remove(USER_ID)
        editor.remove(USER_EMAIL)
        editor.remove(USER_NAME)
        editor.remove(USER_PASSWORD)
        editor.remove("nickname")
        editor.remove("point")
        editor.remove("gamesWon")
        editor.remove("totalGames")
        editor.remove("level")
        editor.remove("exp")
        editor.remove("token")
        editor.remove(COOKIES_KEY_NAME)
        // MY_ROOM_ITEMS는 삭제하지 않음
        
        editor.apply()
    }

    fun addUserCookie(cookies: HashSet<String>) {
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun deleteUserCookie() {
        editor.remove(COOKIES_KEY_NAME)
        editor.apply()
    }

    fun saveMyRoomItems(items: List<MyRoomItem>) {
        val userId = getUser().userId
        val key = "my_room_items_$userId"  // 유저별 고유 키
        
        val json = gson.toJson(items)
        Log.d("SharedPreferencesUtil", "Saving items: $json")
        editor.putString(key, json)
        editor.apply()
    }

    fun getMyRoomItems(): List<MyRoomItem> {
        val userId = getUser().userId
        val key = "my_room_items_$userId"  // 유저별 고유 키
        
        val json = preferences.getString(key, "[]")
        Log.d("SharedPreferencesUtil", "Loading items: $json")
        val type = object : TypeToken<List<MyRoomItem>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getToken(): String {
        return preferences.getString("token", "") ?: ""
    }
}