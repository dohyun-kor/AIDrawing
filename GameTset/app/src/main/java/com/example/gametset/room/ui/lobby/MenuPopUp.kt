package com.example.gametset.room.ui.lobby

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gametset.R
import com.example.gametset.databinding.FragmentMenuPopUpBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.Friend
import com.example.gametset.room.data.model.dto.FriendRequest
import com.example.gametset.room.data.model.dto.SearchFriendInfo
import com.example.gametset.room.data.model.response.UserResponse
import com.example.gametset.room.data.remote.RetrofitUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.gametset.room.ui.setting.SettingModalFragment_jw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.example.gametset.room.ui.friend.FriendPagerAdapter_jw
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.gametset.room.ui.ranking.RankingDialog

private val TAG = "MainActivityViewModel"

class MenuPopUp : Fragment(), FriendRecyclerViewAdapter.OnItemClickListener {
    lateinit var mainActivity: MainActivity
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: FragmentMenuPopUpBinding
    private lateinit var friendRecyclerAdapter: FriendRecyclerViewAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuPopUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendRecyclerAdapter = FriendRecyclerViewAdapter(
            emptyList<Friend>().toMutableList(),
            this,
            viewLifecycleOwner.lifecycleScope
        )
        mainActivityViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // 로딩 상태에 따라 버튼 비활성화/활성화
            binding.friendFrame.isEnabled = !isLoading
            binding.rankingFrame.isEnabled = !isLoading
            binding.logoutFrame.isEnabled = !isLoading
            // 추가적인 버튼들도 동일하게 처리
        }

        with(binding) {
            //설정
            settingsFrame.setOnClickListener {
                // 설정 모달 프래그먼트 생성 및 표시
                val settingModalFragment_jw = SettingModalFragment_jw()
                settingModalFragment_jw.show(parentFragmentManager, "SettingModal")
                parentFragmentManager.popBackStack()
            }

            //친구
            friendFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
                setupFriendDialog()
            }

            //랭킹
            rankingFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
                RankingDialog(mainActivity).show()
            }

            // 로그아웃
            logoutFrame.setOnClickListener {
                parentFragmentManager.popBackStack()
                mainActivity.logout()
            }

            //배경 눌렀을때 뒤로 가기
            dialogLayout.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        mainActivityViewModel.friendList.observe(viewLifecycleOwner) { friendList ->
            Log.d("변화", "Friend list updated: $friendList")
            friendRecyclerAdapter.setFriends(friendList.toMutableList())
            friendRecyclerAdapter.notifyDataSetChanged()
        }
        mainActivityViewModel.getFriendList()
    }

    private fun setupFriendDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_friend_list, null)
        val dialog = Dialog(requireContext()).apply {
            setContentView(dialogView)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }

        // ViewPager2 설정
        val viewPager = dialogView.findViewById<ViewPager2>(R.id.friendViewPager)
        val tabLayout = dialogView.findViewById<TabLayout>(R.id.friendTabLayout)
        
        viewPager.adapter = FriendPagerAdapter_jw(requireActivity())

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "친구목록"
                1 -> "받은 요청"
                2 -> "보낸 요청"
                else -> null
            }
        }.attach()

        // 닫기 버튼
        dialogView.findViewById<ImageButton>(R.id.close_btn).setOnClickListener {
            dialog.dismiss()
        }

        // 친구 추가 버튼
        dialogView.findViewById<ImageButton>(R.id.addFriendButton).setOnClickListener {
            showAddFriendDialog()
        }

        dialog.show()
    }

    private fun showSearchResult(searchResult: SearchFriendInfo, dialog: Dialog) {
        try {
            val searchResultLayout = dialog.findViewById<ConstraintLayout>(R.id.searchResultLayout)
            val profileImage = dialog.findViewById<ImageView>(R.id.profileImage)
            val nicknameText = dialog.findViewById<TextView>(R.id.nicknameText)
            val addButton = dialog.findViewById<ImageButton>(R.id.addButton)

            mainActivity.runOnUiThread {
                searchResultLayout?.visibility = View.VISIBLE
                nicknameText?.text = searchResult.nickname
                
                Glide.with(mainActivity)
                    .load(searchResult.profileLink)
                    .placeholder(R.drawable.user_profile)
                    .error(R.drawable.user_profile)
                    .into(profileImage)

                addButton?.setOnClickListener {
                    val friendRequest = FriendRequest(
                        ApplicationClass.sharedPreferencesUtil.getUser().userId,
                        searchResult.userId
                    )
                    mainActivityViewModel.requestFriend(friendRequest)
                    searchResultLayout?.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error showing search result", e)
            mainActivity.runOnUiThread {
                Toast.makeText(mainActivity, "검색 결과를 표시하는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddFriendDialog() {
        val dialogView = mainActivity.layoutInflater.inflate(R.layout.dialog_add_friend_jw, null)
        val dialog = Dialog(mainActivity).apply {
            setContentView(dialogView)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setGravity(android.view.Gravity.CENTER)
            }
            setCancelable(true)
        }

        val searchButton = dialogView.findViewById<ImageButton>(R.id.searchButton)
        val searchEditText = dialogView.findViewById<EditText>(R.id.searchEditText)
        val searchResultLayout = dialogView.findViewById<ConstraintLayout>(R.id.searchResultLayout)

        searchButton.setOnClickListener {
            val searchText = searchEditText.text.toString().trim()
            if (searchText.isNotEmpty()) {
                mainActivity.lifecycleScope.launch {
                    try {
                        searchResultLayout.visibility = View.GONE
                        mainActivity.runOnUiThread {
                            Toast.makeText(mainActivity, "검색중...", Toast.LENGTH_SHORT).show()
                        }

                        // 닉네임으로 사용자 정보 조회
                        val userInfo = RetrofitUtil.userService.getUserDetailsByNickname(searchText)
                        Log.d(TAG, "Found user: $userInfo")
                        
                        showAddFriendResult(
                            SearchFriendInfo(
                                userId = userInfo.userId,
                                nickname = userInfo.nickname,
                                profileLink = ""  // 프로필 이미지는 빈 문자열로 설정
                            ),
                            dialog
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "User search failed", e)
                        mainActivity.runOnUiThread {
                            Toast.makeText(mainActivity, "사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(mainActivity, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<ImageButton>(R.id.closeButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAddFriendResult(searchResult: SearchFriendInfo, dialog: Dialog) {
        try {
            val searchResultLayout = dialog.findViewById<ConstraintLayout>(R.id.searchResultLayout)
            val profileImage = dialog.findViewById<ImageView>(R.id.profileImage)
            val nicknameText = dialog.findViewById<TextView>(R.id.nicknameText)
            val addButton = dialog.findViewById<ImageButton>(R.id.addButton)

            mainActivity.runOnUiThread {
                searchResultLayout.visibility = View.VISIBLE
                nicknameText.text = searchResult.nickname

                Glide.with(mainActivity)
                    .load(searchResult.profileLink)
                    .placeholder(R.drawable.user_profile)
                    .error(R.drawable.user_profile)
                    .into(profileImage)

                addButton.setOnClickListener {
                    val myUserId = ApplicationClass.sharedPreferencesUtil.getUser().userId
                    val friendRequest = FriendRequest(myUserId, searchResult.userId)
                    mainActivityViewModel.requestFriend(friendRequest)
                    Toast.makeText(mainActivity, "친구 요청을 보냈습니다.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    mainActivityViewModel.getFriendList()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error showing add friend result", e)
            mainActivity.runOnUiThread {
                Toast.makeText(mainActivity, "친구 요청에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.isOpenMenu = true
    }

    override fun onStop() {
        super.onStop()
        mainActivity.isOpenMenu = false
    }

    override fun onAcceptClick(position: Int) {
        // 수락 버튼 클릭 처리
        var friend = friendRecyclerAdapter.getFriendAtPosition(position)
        friend.status = "ACCEPTED"
        mainActivityViewModel.acceptFriend(friend)
//        mainActivityViewModel.getFriendList()
        friendRecyclerAdapter.notifyItemChanged(position)
    }

    override fun onRejectClick(position: Int) {
        // 거절 버튼 클릭 처리
        var friend = friendRecyclerAdapter.getFriendAtPosition(position)
        friend.status = "BLOCKED"
        mainActivityViewModel.acceptFriend(friend)
        // 친구 리스트에서 해당 친구를 삭제
        friendRecyclerAdapter.deleteFriend(position)
        mainActivityViewModel.getFriendList()
//        friendRecyclerAdapter.notifyItemRemoved(position)
        friendRecyclerAdapter.notifyItemChanged(position)
    }
}