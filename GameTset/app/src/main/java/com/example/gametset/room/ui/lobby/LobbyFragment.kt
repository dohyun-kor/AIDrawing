package com.example.gametset.room.ui.lobby

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gametset.R
import com.example.gametset.databinding.FragmentLobbyBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.base.ApplicationClass.Companion.retrofit
import com.example.gametset.room.base.ApplicationClass.Companion.sharedPreferencesUtil
import com.example.gametset.room.data.model.dto.OneRoomDto
import com.example.gametset.room.data.model.dto.RoomDto
import com.example.gametset.room.data.remote.RetrofitUtil
import com.example.gametset.room.ui.gameRoom.PlayGameFragment
import com.example.gametset.room.websocket.GameWebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import org.json.JSONObject

class LobbyFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: LobbyRecyclerViewAdapter
    private val isRefreshEnabled = AtomicBoolean(true)
    private val REFRESH_COOLDOWN = 3000L // 3초
    private lateinit var webSocketManager: GameWebSocketManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 메인 배경음악 재생 확인
        mainActivity.playBackgroundMusic()

        setupRecyclerView()
        setupClickListeners()
        observeRoomList()
    }

    private fun setupRecyclerView() {
        adapter = LobbyRecyclerViewAdapter()
        binding.roomList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@LobbyFragment.adapter
        }

        adapter.setOnItemClickListener { selectroom ->
            viewModel.getOneRoomInfo(selectroom.roomId) { room ->
                if (room == null) {
                    // ✅ 방 정보가 없을 경우 (삭제되었거나 null일 경우)
                    Toast.makeText(context, "방에 입장할 수 없습니다!", Toast.LENGTH_SHORT).show()
                    refreshRoomList()
                    return@getOneRoomInfo
                }

                Log.d("roomTest", "${room.status} is status and ${room.nowPlayers} / ${room.maxPlayers} ")

                if (room.status == "PLAY" || room.nowPlayers >= room.maxPlayers) {
                    // ✅ 방이 진행 중이거나 최대 인원 초과 시 입장 불가
                    Toast.makeText(context, "방에 입장할 수 없습니다!", Toast.LENGTH_SHORT).show()
                    refreshRoomList()
                } else {
                    // ✅ WebSocket join 메시지 전송
                    val user = sharedPreferencesUtil.getUser()
                    val webSocket = GameWebSocketManager.getInstance().getWebSocket()

                    val json = JSONObject().apply {
                        put("event", "join")
                        put("userId", user.userId)
                        put("roomId", room.roomId)
                    }
                    webSocket?.send(json.toString())

                    // ✅ WebSocket 응답 처리 (기존 방 유저 정보 수신)
                    GameWebSocketManager.getInstance().addExistingRoomUserListeners { response ->
                        val hostId = response.optString("hostId", "-1").toInt()
                        val userArray = response.getJSONArray("users")
                        activity?.runOnUiThread {
                            // ✅ 게임 음악 시작
                            mainActivity.startGameMusic()

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.frame_layout_main,
                                    PlayGameFragment.newInstance(room.roomId, hostId, userArray)
                                )
                                .commit()
                        }
                    }
                }
            }
        }

    }

    private fun setupClickListeners() {
        binding.createButton.setOnClickListener {
            RoomCreateModalFragment().show(parentFragmentManager, null)
        }

        binding.refreshButton.setOnClickListener {
            if (isRefreshEnabled.get()) {
                refreshRoomList()
            }
        }
    }

    private fun refreshRoomList() {
        // 리프레시 버튼 비활성화
        isRefreshEnabled.set(false)
        binding.refreshButton.alpha = 0.5f
        binding.refreshButton.isClickable = false

        // 방 목록 새로고침
        viewModel.getRoom()

        // 3초 후 리프레시 버튼 다시 활성화
        CoroutineScope(Dispatchers.Main).launch {
            delay(REFRESH_COOLDOWN)
            isRefreshEnabled.set(true)
            binding.refreshButton.alpha = 1.0f
            binding.refreshButton.isClickable = true
        }
    }

    private fun observeRoomList() {
        viewModel.roomList.observe(viewLifecycleOwner) { rooms ->
            adapter.updateRooms(rooms)
        }
        // 초기 방 목록 로드
        viewModel.getRoom()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.playBackgroundMusic()
        mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_page_2
        mainActivity.hideBottomNav(false)
        mainActivity.hideToolBar(false)
    }

    override fun onStop() {
        super.onStop()
        mainActivity.hideBottomNav(true)
        mainActivity.hideToolBar(true)

        // 앱이 백그라운드로 가거나 종료될 때만 웹소켓 연결을 끊음
        if (requireActivity().isFinishing) {
            webSocketManager.disconnect()
        }
    }


}