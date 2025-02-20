package com.example.gametset.room.ui.friend

import android.os.Bundle
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

class ReceivedRequestFragment_jw : Fragment(), FriendRecyclerViewAdapter.OnItemClickListener {
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var friendRecyclerAdapter: FriendRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_received_request_jw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.receivedRequestRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        friendRecyclerAdapter = FriendRecyclerViewAdapter(
            mutableListOf<Friend>(),
            this,  // this를 리스너로 전달
            viewLifecycleOwner.lifecycleScope
        )
        recyclerView.adapter = friendRecyclerAdapter

        // 받은 요청 목록 관찰
        mainActivityViewModel.receivedRequests.observe(viewLifecycleOwner) { friends ->
            friendRecyclerAdapter.setFriends(friends.toMutableList())
            friendRecyclerAdapter.notifyDataSetChanged()
        }
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