package com.example.gametset.room.ui.friend

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FriendPagerAdapter_jw(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FriendListFragment_jw()
            1 -> ReceivedRequestFragment_jw()
            2 -> SentRequestFragment_jw()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
} 