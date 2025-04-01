package com.example.gametset.room.data.model.dto

import android.provider.ContactsContract.CommonDataKinds.Nickname

data class Friend(
    val userId: Int,
    val friendId: Int,
    var status: String,  // "PENDING", "SENT", "ACCEPTED", "BLOCKED"
    var nickname: String = "",
    var userProfileUrl: String = ""
)