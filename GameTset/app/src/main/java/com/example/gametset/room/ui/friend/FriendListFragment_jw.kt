package com.example.gametset.room.ui.friend

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.ui.lobby.FriendRecyclerViewAdapter
import androidx.lifecycle.lifecycleScope
import com.example.gametset.room.data.model.dto.Friend
import com.example.gametset.room.MainActivity
import com.example.gametset.room.ui.myroom.FriendMyroomFragment_jw

class FriendListFragment_jw : Fragment(), FriendRecyclerViewAdapter.OnItemClickListener {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var friendRecyclerAdapter: FriendRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friend_list_jw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.friendRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        friendRecyclerAdapter = FriendRecyclerViewAdapter(
            mutableListOf<Friend>(),
            this,  // this를 리스너로 전달
            viewLifecycleOwner.lifecycleScope
        )
        recyclerView.adapter = friendRecyclerAdapter

        // 수락된 친구 목록 관찰
        mainActivityViewModel.acceptedFriends.observe(viewLifecycleOwner) { friends ->
            friendRecyclerAdapter.setFriends(friends.toMutableList())
            friendRecyclerAdapter.notifyDataSetChanged()
        }

        // 친구 목록 갱신
        mainActivityViewModel.getFriendList()

        friendRecyclerAdapter.setOnFriendMyroomVisitListener(object : FriendRecyclerViewAdapter.OnFriendMyroomVisitListener {
            override fun onMyroomVisit(friendId: Int) {
                mainActivity.openFriendMyroom(friendId)
            }
        })
    }

    // OnItemClickListener 구현
    override fun onAcceptClick(position: Int) {
        val friend = friendRecyclerAdapter.getFriendAtPosition(position)
        friend.status = "ACCEPTED"
        mainActivityViewModel.acceptFriend(friend)
        friendRecyclerAdapter.notifyItemChanged(position)
    }

    override fun onRejectClick(position: Int) {
        val friend = friendRecyclerAdapter.getFriendAtPosition(position)
        friend.status = "BLOCKED"
        mainActivityViewModel.acceptFriend(friend)
        friendRecyclerAdapter.notifyItemChanged(position)
    }
} 