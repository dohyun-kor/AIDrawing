package com.example.gametset.room.ui.login

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.gametset.room.data.remote.RetrofitUtil
import com.example.gametset.room.data.model.dto.UserDto

private const val TAG = "Login_싸피"

sealed class LoginResult {
    data class Success(val user: UserDto) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class LoginFragmentViewModel {
    private var currentToken: String? = null

    fun login(id: String, pass: String, callback: (LoginResult) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginDto = UserDto(id, pass)
                val response = RetrofitUtil.userService.login(loginDto)

                if (response.userId != -1) {
                    val userInfo = RetrofitUtil.userService.getUserInfo(response.userId)
                    currentToken = response.token

                    val user = UserDto(
                        response.userId,
                        "",
                        userInfo.id,
                        userInfo.id,
                        userInfo.nickname,
                        userInfo.points,
                        userInfo.gameswon,
                        userInfo.totalgames,
                        userInfo.level,
                        userInfo.exp,
                        userInfo.userProfileItemId,
                        response.token
                    )
                    callback(LoginResult.Success(user))
                } else {
                    Log.d(TAG, "로그인 실패")
                    callback(LoginResult.Error("Invalid credentials"))
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: ${e.message}")
                callback(LoginResult.Error(e.message ?: "Unknown error occurred"))
            }
        }
    }

    fun getCurrentToken(): String? = currentToken
}