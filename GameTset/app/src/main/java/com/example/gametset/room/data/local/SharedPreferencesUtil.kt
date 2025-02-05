package com.example.gametset.room.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.gametset.room.data.model.dto.UserDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesUtil(context: Context) {
    val SHARED_PREFERENCES_NAME = "smartstore_preference"
    val COOKIES_KEY_NAME = "cookies"

    private val gson = Gson()
    private val MAX_NOTIFICATIONS = 50

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    //사용자 정보 저장
    fun addUser(user: UserDto) {
        val editor = preferences.edit()
        editor.putString("id", user.id)
        editor.putString("nickname", user.nickname)
        editor.apply()
    }

    fun getUser(): UserDto {
        val id = preferences.getString("id", "")
        if (id != "") {
            val name = preferences.getString("nickname", "")
            return UserDto(0,"", id!!, "", "", 0, 0, 0, 0, 0)
        } else {
            return UserDto()
        }
    }

    fun deleteUser() {
        //preference 지우기
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun addUserCookie(cookies: HashSet<String>) {
        val editor = preferences.edit()
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }

}