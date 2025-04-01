package com.example.gametset.room.data.model

import android.graphics.PointF
import com.example.gametset.room.data.model.GuestBook

data class ItemInfo(
    val artistName: String = "",
    val artworkName: String = "",
    val artworkType: String = "",
    val description: String = "",
    val guestBooks: List<GuestBook> = listOf()
) {
    fun updateGuestBooks(newGuestBooks: List<GuestBook>): ItemInfo {
        return copy(guestBooks = newGuestBooks)
    }
} 