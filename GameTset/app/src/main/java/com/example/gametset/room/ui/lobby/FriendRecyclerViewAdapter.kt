package com.example.gametset.room.ui.lobby

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gametset.R
import com.example.gametset.databinding.ItemFriendBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.Friend
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

private val TAG = "FriendRecyclerViewAdapter"

class FriendRecyclerViewAdapter(
    private var friends: MutableList<Friend>,
    private val listener: OnItemClickListener,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW_TYPE_NORMAL = 0
    private val ITEM_VIEW_TYPE_SENT_REQUEST = 1

    interface OnItemClickListener {
        fun onAcceptClick(position: Int)
        fun onRejectClick(position: Int)
        //
    }

    interface OnFriendMyroomVisitListener {
        fun onMyroomVisit(friendId: Int)
    }

    private var myroomVisitListener: OnFriendMyroomVisitListener? = null

    fun setOnFriendMyroomVisitListener(listener: OnFriendMyroomVisitListener) {
        myroomVisitListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        val friend = friends[position]
        return if (friend.status == "PENDING" && friend.userId == ApplicationClass.sharedPreferencesUtil.getUser().userId) {
            ITEM_VIEW_TYPE_SENT_REQUEST
        } else {
            ITEM_VIEW_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_SENT_REQUEST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sent_friend_request_jw, parent, false)
                SentRequestViewHolder(view)
            }
            else -> {
                val binding = ItemFriendBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FriendViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val friend = friends[position]
        when (holder) {
            is SentRequestViewHolder -> {
                holder.bind(friend)
            }
            is FriendViewHolder -> {
                holder.bind(friend)
                holder.binding.btnVisitMyroom.setOnClickListener {
                    if(friend.userId == ApplicationClass.sharedPreferencesUtil.getUser().userId){
                        myroomVisitListener?.onMyroomVisit(friend.friendId)
                    }else{
                        myroomVisitListener?.onMyroomVisit(friend.userId)
                    }
                }
            }
        }
    }

    fun setFriends(newFriend: MutableList<Friend>) {
        friends = newFriend
        notifyDataSetChanged()
        Log.d("변화", "Adapter data updated: $newFriend") // 어댑터 데이터가 업데이트된 후 로그
    }

    fun deleteFriend(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            friends.removeAt(position)  // 친구 리스트에서 해당 친구 삭제
            notifyItemRemoved(position)    // 삭제된 아이템을 리사이클러뷰에 알림
        }
    }

    // position을 통해 친구 객체를 가져오는 메서드
    fun getFriendAtPosition(position: Int): Friend {
        return friends[position]
    }

    inner class SentRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        private val nicknameText: TextView = itemView.findViewById(R.id.nicknameText)
        private val cancelButton: ImageButton = itemView.findViewById(R.id.cancelButton)

        fun bind(friend: Friend) {
            nicknameText.text = friend.nickname
            Glide.with(itemView.context)
                .load(friend.userProfileUrl)
                .placeholder(R.drawable.user_profile)
                .into(profileImage)

            cancelButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 친구 요청 취소
                    listener.onRejectClick(position)
                    // 리스트에서 제거
                    friends.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    inner class FriendViewHolder(val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.friendAcceptanceBtn.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onAcceptClick(position)
                }
            }
            binding.friendRejectionBtn.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRejectClick(position)
                }
            }
            binding.friendDeleteBtn.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRejectClick(position)
                }
            }
        }

        fun bind(friend: Friend) {
            with(binding) {
                if (friend.status != "BLOCKED") {
                    Log.d("변화", "Setting friend list for: ${friend.nickname}")

                    userNicknameTextView.text = friend.nickname
                    Log.d("변화", "Nickname set: ${friend.nickname}")
                    Glide.with(binding.root.context)
                        .load(friend.userProfileUrl)
                        .placeholder(R.drawable.user_profile)
                        .error(R.drawable.user_profile)
                        .into(userProfile)
                    Log.d("변화", "Image loaded: ${friend.userProfileUrl}")

                    if (friend.status == "ACCEPTED") {
                        friendAcceptanceBtn.visibility = View.GONE
                        acceptTextview.visibility = View.GONE
                        friendRejectionBtn.visibility = View.GONE
                        rejectTextView.visibility = View.GONE
                        friendDeleteBtn.visibility=View.VISIBLE
                    } else {
                        friendAcceptanceBtn.visibility = View.VISIBLE
                        acceptTextview.visibility = View.VISIBLE
                        friendRejectionBtn.visibility = View.VISIBLE
                        rejectTextView.visibility = View.VISIBLE
                        friendDeleteBtn.visibility=View.GONE
                    }
                    Log.d("변화", "Visibility set for: ${friend.status}")
                }
            }
        }
    }
}