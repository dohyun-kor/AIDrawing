package com.example.gametset.room.util

import android.content.Context
import android.content.SharedPreferences
import com.example.gametset.room.data.model.MyRoomItem
import com.google.gson.Gson

class SharedPreferencesUtil(context: Context) {
    private val SHARED_PREFERENCES_NAME = "game_preference"
    private val preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()
    private val gson = Gson()

    companion object {
        private const val USER_ID = "userId"
        private const val USER_PASSWORD = "userPassword"
        private const val USER_NAME = "userName"
        private const val USER_EMAIL = "userEmail"
        private const val USER_COOKIE = "userCookie"
        private const val MY_ROOM_ITEMS = "my_room_items"
        private const val BGM_VOLUME = "bgm_volume"
        private const val SFX_VOLUME = "sfx_volume"
        private const val VIBRATION_ENABLED = "vibration_enabled"
    }

    fun saveMyRoomItems(items: List<MyRoomItem>) {
        editor.putString(MY_ROOM_ITEMS, MyRoomItem.toJson(items))
        editor.apply()
    }

    fun getMyRoomItems(): List<MyRoomItem> {
        val json = preferences.getString(MY_ROOM_ITEMS, "")
        return if (json.isNullOrEmpty()) listOf() else MyRoomItem.fromJson(json)
    }

    fun saveBgmVolume(volume: Int) {
        editor.putInt(BGM_VOLUME, volume)
        editor.apply()
    }

    fun getBgmVolume(): Int = preferences.getInt(BGM_VOLUME, 50)

    fun saveSfxVolume(volume: Int) {
        editor.putInt(SFX_VOLUME, volume)
        editor.apply()
    }

    fun getSfxVolume(): Int = preferences.getInt(SFX_VOLUME, 50)

    fun saveVibrationEnabled(enabled: Boolean) {
        editor.putBoolean(VIBRATION_ENABLED, enabled)
        editor.apply()
    }

    fun getVibrationEnabled(): Boolean = preferences.getBoolean(VIBRATION_ENABLED, true)
} 