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
                RetrofitUtil.userService.login(UserDto(id, pass))
            }.onSuccess {
                _user.value = it
            }.onFailure {
                _user.value = UserDto()
            }
        }
    }
}