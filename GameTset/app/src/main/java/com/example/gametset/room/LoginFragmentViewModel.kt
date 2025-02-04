package com.example.gametset.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gametset.room.data.remote.RetrofitUtil
import com.example.gametset.room.model.dto.UserDto
import kotlinx.coroutines.launch

class LoginFragmentViewModel : ViewModel() {
    private val _user = MutableLiveData<UserDto>()

    val user: LiveData<UserDto>
        get() = _user

    fun login(id: String, pass: String) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.userService.login(id, pass)
            }.onSuccess { response ->
                if (response.statusCode == 200 && response.access) {
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
                    println("Login successful! access: ${response.statusCode}")
                } else {
                    // 로그인 실패
                    println("Login failed: ${response.statusCode}")
                }
            }.onFailure { exception ->
                // 로그인 예외 처리
                println("Error occurred: ${exception.message}")
            }
        }
    }
}

