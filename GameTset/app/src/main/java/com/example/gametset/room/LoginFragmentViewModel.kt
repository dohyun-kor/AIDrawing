package com.example.gametset.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gametset.room.data.remote.RetrofitUtil
import com.example.gametset.room.model.dto.LoginDto
import com.example.gametset.room.model.dto.UserDto
import com.example.gametset.room.model.response.LoginResponse
import kotlinx.coroutines.launch

class LoginFragmentViewModel : ViewModel() {
    private val _user = MutableLiveData<UserDto>()
    private val TAG ="Login_싸피"

    val user: LiveData<UserDto>
        get() = _user

    fun login(id: String, pass: String) {
        viewModelScope.launch {
            runCatching {
                val loginDto = UserDto(id, pass)
                RetrofitUtil.userService.login(loginDto)
            }.onSuccess { response ->
                if (response.userId!=-1) {
                    _user.value = UserDto(
                        response.userId,
                        "",
                        "AA",
                        "",
                        "",
                        0,
                        0,
                        0,
                        0,
                        0
                    )
//                    val userinfo = RetrofitUtil.userService.getUserInfo(response.userId)
//                    // 로그인 성공
//                    _user.value = UserDto(
//                        response.userId,
//                        "",
//                        userinfo.id,
//                        "",
//                        userinfo.nickname,
//                        userinfo.points,
//                        userinfo.gameswon,
//                        userinfo.totalgames,
//                        userinfo.level,
//                        userinfo.exp
//                    )
//                    println("Login successful! access: ${response.statusCode}")
                } else {
                    Log.d(TAG, "실패")
                    // 로그인 실패
//                    println("Login failed: ${response.statusCode}")
                }
            }.onFailure { exception ->
                Log.d(TAG, "${exception.message} ")
                // 로그인 예외 처리
                println("Error occurred: ${exception.message}")
            }
        }
    }
}

