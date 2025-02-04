package com.example.gametset.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gametset.room.data.remote.RetrofitUtil
import com.example.gametset.room.model.dto.UserDto
import com.example.gametset.room.model.response.LoginResponse
import kotlinx.coroutines.launch

class LoginFragmentViewModel : ViewModel() {
    private val _user = MutableLiveData<UserDto>()

    val user: LiveData<UserDto>
        get() = _user

    fun login(id: String, pass: String) {
        viewModelScope.launch {
            runCatching {
                LoginResponse(RetrofitUtil.userService.login(id, pass))
            }.onSuccess { response ->
                if (response.userId!=-1) {
                    val userinfo = RetrofitUtil.userService.getUserInfo(response.userId)
                    // 로그인 성공
                    _user.value = UserDto(
                        response.userId,
                        "",
                        userinfo.id,
                        "",
                        userinfo.nickname,
                        userinfo.points,
                        userinfo.gameswon,
                        userinfo.totalgames,
                        userinfo.level,
                        userinfo.exp
                    )
//                    println("Login successful! access: ${response.statusCode}")
                } else {
                    // 로그인 실패
//                    println("Login failed: ${response.statusCode}")
                }
            }.onFailure { exception ->
                Log.d("BBBBBBBBBBB", "lDKSEHL ")
                // 로그인 예외 처리
                println("Error occurred: ${exception.message}")
            }
        }
    }
}

