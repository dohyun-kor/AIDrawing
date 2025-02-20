package com.example.gametset.room.ui.gameRoom

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gametset.R
import com.example.gametset.databinding.ItemRoomUserBinding
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.GameUserInfo
import com.example.gametset.room.data.remote.RetrofitUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


private val TAG = "RoomUserRecyclerViewAdapter_싸피"

@OptIn(DelicateCoroutinesApi::class)
class RoomUserRecyclerViewAdapter(var userIdList: MutableList<GameUserInfo>) :
    RecyclerView.Adapter<RoomUserRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRoomUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = userIdList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = userIdList[position]
        holder.setRoomUserList(userId)
    }

    inner class ViewHolder(val binding: ItemRoomUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setRoomUserList(gameUserInfo: GameUserInfo) {
            binding.gameUserNickName.text = "로딩중..."  // 초기화 텍스트

            // Glide 로딩 임시 처리
            Glide.with(binding.root.context)
                .load(R.drawable.user_profile) // 기본 이미지 로드
                .into(binding.roomUserProfile)

            // 비동기 데이터 처리
            GlobalScope.launch {
                try {
                    val userInfo = RetrofitUtil.userService.getUserInfo(gameUserInfo.userId)
                    withContext(Dispatchers.Main) {
                        binding.gameUserNickName.text = userInfo.nickname
                        binding.userScore.text = gameUserInfo.score.toString() + "pt"

                        val url =
                            RetrofitUtil.storeService.getOneItem(userInfo.userProfileItemId).link
                        Glide.with(binding.root.context)
                            .load(url)
                            .placeholder(R.drawable.user_profile)
                            .error(R.drawable.user_profile)
                            .into(binding.roomUserProfile)

                        itemView.setBackgroundResource(R.drawable.round_backgroud_border_black)
                        if (gameUserInfo.isCorrect) {  // 예시: 정답을 맞춘 경우
                            itemView.background = ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.round_background_border_green
                            )
                        } else {
                            itemView.background = ContextCompat.getDrawable(
                                binding.root.context,
                                R.drawable.round_backgroud_border_black
                            )
                        }

                        if (gameUserInfo.isHostId) {
                            binding.ivRoomManager.visibility = View.VISIBLE
                        } else {
                            binding.ivRoomManager.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.gameUserNickName.text = "정보 없음"  // 에러 처리
                    }
                }
            }
        }
    }

    // 사용자 추가 함수
    fun addRoomUser(gameUserInfo: GameUserInfo, recyclerView: RecyclerView) {
        // 중복 체크
        try {
            if (!userIdList.contains(gameUserInfo)) {
                userIdList.add(gameUserInfo)
                notifyItemInserted(userIdList.size - 1)
                recyclerView.scrollToPosition(userIdList.size - 1)
                Log.d(TAG, "addRoomUser: ${gameUserInfo.userId}")
                Log.d(TAG, "addRoomUser: $userIdList")
            }
        } catch (e: Exception) {
            Log.d(TAG, "removeRoomUser: $e")
        }

    }

    fun removeRoomUser(gameUserInfo: GameUserInfo, recyclerView: RecyclerView) {
        // 중복 체크
        try {
            val index = userIdList.indexOfFirst { it.userId == gameUserInfo.userId }
            if (index != -1) {
                userIdList.removeAt(index)
                notifyItemRemoved(index)
                // notifyItemRangeChanged를 호출하여 position 이후의 아이템들의 위치를 업데이트
                notifyItemRangeChanged(index, userIdList.size)
                recyclerView.scrollToPosition(if (userIdList.isNotEmpty()) userIdList.size - 1 else 0)
                Log.d(TAG, "removeRoomUser: ${gameUserInfo.userId}")
                Log.d(TAG, "removeRoomUser: $userIdList")
            }
        } catch (e: Exception) {
            Log.d(TAG, "removeRoomUser: $e")
        }

    }

    fun updateItemByUserId(userId: Int, score: Int, state: Int, isCorrect: Boolean) {
        val item = userIdList.find { it.userId == userId }
        item?.let {
            if (state == 1) {
                it.score += score
            } else {
                it.score = score
            }
            it.isCorrect = isCorrect
            notifyItemChanged(userIdList.indexOf(it))
        }
    }

    fun updateItemByHostId(hostId: Int) {
        val item = userIdList.find { it.userId == hostId }
        item?.let {
            it.isHostId = true
            notifyItemChanged(userIdList.indexOf(it))
        }
    }
}
