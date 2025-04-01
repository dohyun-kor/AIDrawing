package com.example.gametset.room.ui.gameRoom

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.transition.Transition
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.gametset.R
import com.example.gametset.databinding.FragmentLoginBinding
import com.example.gametset.databinding.FragmentPlayGameBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.WordAdapter
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.GameUserInfo
import com.example.gametset.room.data.model.dto.OneRoomDto
import com.example.gametset.room.data.model.dto.RoomChangeDto
import com.example.gametset.room.data.remote.RetrofitUtil
import com.example.gametset.room.ui.lobby.LobbyFragment
import com.example.gametset.room.ui.lobby.ProfileImageListFragment
import com.example.gametset.room.ui.store.StoreBindingAdapter.loadImage
import com.example.gametset.room.websocket.GameWebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.WebSocket
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.lang.Integer.parseInt

private const val TAG = "PlayGameFragment_싸피"

class PlayGameFragment : Fragment(), OutFragment.OutDialogListener {
    private val viewModel: MainActivityViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity

    // 뷰 바인딩 객체
    private var _binding: FragmentPlayGameBinding? = null
    private val binding get() = _binding!!
    private var roomId: Int = -1 // roomId 변수 추가
    private var hostId: Int = -1
    private var userArray: MutableList<GameUserInfo> = mutableListOf()
    private var myUserId: Int = -1
    private lateinit var waitingScreen: View
    private lateinit var topicScreen: View

    //    private lateinit var remainTime: TextView
    private lateinit var topic1: TextView
    private lateinit var topic2: TextView
    private lateinit var roomRemainTimeScreen: View
    private lateinit var roomSettingMode: TextView
    private lateinit var roomSettingRound: TextView
    private lateinit var roomSettingRemainTime: TextView
    private lateinit var roomSettingLevel: TextView
    private lateinit var roomTurnTopic: TextView

    private var isCanStartGame: Boolean = false
    private var isMyTurn: Boolean = false
    private lateinit var myRoomInfo: OneRoomDto

    private var myScore: Int = 0
    private var currentGameWinnerUserId: Int = -1
    private var isStartGame: Boolean = false

    companion object {
        fun newInstance(roomId: Int, hostId: Int, userArray: JSONArray): PlayGameFragment {
            return PlayGameFragment().apply {
                arguments = Bundle().apply {
                    putInt("roomId", roomId)
                    putInt("hostId", hostId)  // hostId 추가
                    putString("userArray", userArray.toString())
                }
            }
        }
    }

    // 어댑터 객체
    private lateinit var wordAdapter: WordAdapter
    private lateinit var roomUserAdapter: RoomUserRecyclerViewAdapter

    // 단어 목록
    private val wordList = mutableListOf<CharSequence>()
    private val roomUserList = mutableListOf<GameUserInfo>()

    // 단어 목록
    private var currentColor = 0

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                saveDrawing()
            } else {
                Toast.makeText(context, "저장 권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomId = it.getInt("roomId", -1)
            hostId = it.getInt("hostId", -1)  // hostId 초기화

            val jsonString = it.getString("userArray")
            val jsonArray = JSONArray(jsonString)

            myUserId = ApplicationClass.sharedPreferencesUtil.getUser().userId  // 현재 유저 ID 초기화

            // JSONArray를 List<Int>로 변환
            for (i in 0 until jsonArray.length()) {
                var isHostId = false
                Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaa", "onCreate: ${JSONArray(jsonString)}")
                if (jsonArray.getString(i).toInt() == hostId) {
                    isHostId = true
                    Log.d("aaaaaaaaaaaaaaaaaaaaaaaaaa", "onCreate: $isHostId")
                }
                val gameUserInfo = GameUserInfo(jsonArray.getString(i).toInt(), 0, false, isHostId)
                userArray.add(gameUserInfo)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlayGameBinding.bind(view)

        if (myUserId == hostId) {
            binding.ivRoomManager.visibility = View.VISIBLE
        } else {
            binding.ivRoomManager.visibility = View.GONE
        }

        binding.drawingView.passingRoomId(roomId)

        wordAdapter = WordAdapter(wordList)
        binding.wordRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.wordRecyclerView.adapter = wordAdapter
        currentColor = ContextCompat.getColor(requireContext(), R.color.black)

        // DrawingView에 WebSocket 전달
        binding.drawingView.setWebSocket(GameWebSocketManager.getInstance().getWebSocket())

        Log.d(TAG, "exsisting list: $userArray")

        viewModel.roomInfo.observe(viewLifecycleOwner) { roomInfo ->
            roomInfo?.let {
                // UI 업데이트 코드
                binding.gameRoomName.text = roomInfo.roomName
                roomSettingMode.text = roomInfo.mode
                roomSettingRound.text = roomInfo.rounds.toString()
                roomSettingRemainTime.text = roomInfo.roundTime.toString()
                roomSettingLevel.text = roomInfo.level
            }
        }

        // 웹소켓 리스너 추가
        GameWebSocketManager.getInstance().addDrawingEventListener { json ->
            val event = json.getString("event")
            val messageRoomId = json.optInt("roomId", -1)

            // 현재 방의 메시지만 처리
            if (messageRoomId == roomId) {
                viewLifecycleOwner.lifecycleScope.launch {
//                activity?.runOnUiThread {
                    when (event) {
                        "draw" -> {
                            val x = json.optDouble("x", -1.0).toFloat()
                            val y = json.optDouble("y", -1.0).toFloat()
                            val color = json.optString("color", "#000000")
                            val mode = json.optInt("mode", 1)
                            if (x >= 0 && y >= 0) {
                                binding.drawingView.drawFromServer(
                                    x,
                                    y,
                                    color,
                                    mode,
                                    binding.verticalSeekBar.progress.toFloat()
                                )
                            }
                        }

                        "cleardrawing" -> {
                            binding.drawingView.clearDrawing()
                        }
                    }
                }
            }
        }

        // 채팅 메시지 수신 리스너 등록
        GameWebSocketManager.getInstance().addMessageEventListener { json ->
            val messageRoomId = json.optInt("roomId", -1)
            if (messageRoomId == roomId) {
                viewLifecycleOwner.lifecycleScope.launch {
//                activity?.runOnUiThread {
                    val message = json.optString("message", "")
                    val userId = json.optString("userId", "")
                    wordAdapter.addWord(message, binding.wordRecyclerView)
                }
            }
        }

        // ysw
        binding.roomUserBox.visibility = View.VISIBLE
        binding.drawBox.visibility = View.GONE
        binding.startButton.visibility = View.VISIBLE
        binding.answer.visibility = View.VISIBLE

        GameWebSocketManager.getInstance().addColorChangeListeners { json ->
            val messageRoomId = json.optInt("roomId", -1)
            val changedColor = json.optString("color", "#FFFFFF")
            val colorInt = try {
                android.graphics.Color.parseColor(changedColor)  // #FFFFFF 같은 색상 코드를 Int로 변환
            } catch (e: IllegalArgumentException) {
                // 색상 코드가 유효하지 않으면 기본 색상으로 설정
                ContextCompat.getColor(requireContext(), R.color.black)
            }
            if (messageRoomId == roomId) {
                viewLifecycleOwner.lifecycleScope.launch {
//                activity?.runOnUiThread {
                    currentColor = colorInt
                    binding.drawingView.drawPaint.color = currentColor
                }
            }
        }

        binding.gameMyNickName.text = ApplicationClass.sharedPreferencesUtil.getUser().nickname
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d(
                    TAG,
                    "onViewCreated: ${ApplicationClass.sharedPreferencesUtil.getUser().userProfileItemId}"
                )
                var userInfo =
                    RetrofitUtil.userService.getUserInfo(ApplicationClass.sharedPreferencesUtil.getUser().userId)
                val url = RetrofitUtil.storeService.getOneItem(userInfo.userProfileItemId).link
                Log.d(TAG, "onViewCreated: $url")
                Glide.with(binding.root.context)
                    .load(url)
                    .placeholder(R.drawable.user_profile)
                    .error(R.drawable.user_profile)
                    .into(binding.roomMyProfile)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile image", e)
            }
        }

//        roomUserList =
        roomUserAdapter = RoomUserRecyclerViewAdapter(roomUserList)
        binding.roomUserRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.roomUserRecyclerView.adapter = roomUserAdapter
        if (userArray.size != 0) {
            Log.d(TAG, "zzzz: $userArray")
            for (i in 0 until userArray.size) {
                if (ApplicationClass.sharedPreferencesUtil.getUser().userId != userArray[i].userId) {
                    val userId = userArray[i]
                    roomUserAdapter.addRoomUser(
                        userId,
                        binding.roomUserRecyclerView
                    )
                }
            }
        }

        // 새로운 유저 입장 처리
        GameWebSocketManager.getInstance().addLeaveListeners { json ->
            val leavedUserId = json.optInt("userId", -1)
            if (isAdded && view != null && viewLifecycleOwner.lifecycle.currentState.isAtLeast(
                    Lifecycle.State.STARTED
                )
            ) {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        if (myUserId != leavedUserId) {
                            viewLifecycleOwner.lifecycleScope.launch {
//                            activity?.runOnUiThread {
                                Log.d("WebSocket", "playleave: $leavedUserId")
                                var gameUserInfo = GameUserInfo(leavedUserId, 0, false, false)
                                roomUserAdapter.removeRoomUser(
                                    gameUserInfo,
                                    binding.roomUserRecyclerView
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "onViewCreated: $e")
                    }
                }
            }
        }

//        // 사용자 데이터를 비동기로 가져오기
//        viewLifecycleOwner.lifecycleScope.launch {
//            try {
//                // 예: userId 리스트를 가져오는 비동기 호출
//                val userIdList = fetchUserIds()
//                // 어댑터에 사용자 리스트 전달
//                roomUserAdapter.updateUserList(userIdList)
//            } catch (e: Exception) {
//                // 에러 처리
//            }
//        }


        // 펜,지우개 모드
        binding.apply {
            pen.setOnClickListener {
                binding.drawingView.drawPaint.color = currentColor
                binding.drawingView.eraserEnabled = false

                // 버튼 배경색 토글
                pen.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                eraser.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                ) // 기본 배경색

            }
            eraser.setOnClickListener {
                binding.drawingView.drawPaint.color =
                    ContextCompat.getColor(requireContext(), R.color.white)
                sendColorChangeMessage("#FFFFFF")
                binding.drawingView.eraserEnabled = true

                // 버튼 배경색 토글
                eraser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                pen.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                ) // 기본 배경색

            }
        }
        // Start 버튼 클릭 시 메시지 전송
        binding.startButton.setOnClickListener {
            val text = binding.answer.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "입력창이 비었습니다!", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(text)
                binding.answer.text.clear()
//                wordAdapter.addWord(text, binding.wordRecyclerView)
            }
        }

        // Clear 버튼 클릭 시 그림판 지우기
        binding.clearButton.setOnClickListener {
            binding.drawingView.sendClearDrawing()
        }
        // 방 나가기 버튼
        binding.backBtn.setOnClickListener {
            val outFragment = OutFragment()
            outFragment.setOutDialogListener(this)
            outFragment.show(parentFragmentManager, "OutFragment")
        }


        // 색상 선택 버튼 클릭 이벤트 처리
        binding.apply {
            setupColorButton(penColorRed, R.color.red, "#FF0000")
            setupColorButton(penColorOrange, R.color.orange, "#FF7F50")
            setupColorButton(penColorYellow, R.color.yellow, "#FFFF00")
            setupColorButton(penColorGreen, R.color.green, "#ADFF2F")
            setupColorButton(penColorBlue, R.color.blue, "#0000FF")
            setupColorButton(penColorMint, R.color.mint, "#FF00CFC3")
            setupColorButton(penColorPurple, R.color.purple, "#800080")
            setupColorButton(penColorBlack, R.color.black, "#FF000000")
        }

        binding.verticalSeekBar.setProgress(10)
        binding.drawingView.drawPaint.strokeWidth = binding.verticalSeekBar.progress.toFloat()
        binding.drawingView.erasePaint.strokeWidth = binding.verticalSeekBar.progress.toFloat()

        binding.verticalSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // 사용자가 SeekBar를 터치하기 시작할 때 호출됩니다.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // 사용자가 SeekBar를 터치한 후 손을 뗄 때 호출됩니다.
                val progress = seekBar.progress
                setPencilSize(progress.toFloat()) // SeekBar의 현재 값을 연필 크기로 설정
                val json = JSONObject().apply {
                    put("event", "strokechange")
                    put("roomId", roomId)
                    put("stroke", progress.toFloat())
                    put("userId", myUserId)
                }
                GameWebSocketManager.getInstance().getWebSocket()?.send(json.toString())
            }
        })

        // JS
        // 대기 화면 초기화 부분 수정
        waitingScreen = layoutInflater.inflate(R.layout.waiting_screen, null)
        binding.waitingFrame.addView(waitingScreen)  // waitingFrame에 추가
        waitingScreen.visibility = View.GONE

        topicScreen = layoutInflater.inflate(R.layout.topic_screen, null)
        binding.waitingFrame.addView(topicScreen)  // waitingFrame에 추가
        topicScreen.visibility = View.GONE
//        remainTime = topicScreen.findViewById<TextView>(R.id.remaining_time)

        roomRemainTimeScreen = layoutInflater.inflate(R.layout.remain_time_screen, null)
        binding.remainTimeFrame.addView(roomRemainTimeScreen)
        roomSettingMode = roomRemainTimeScreen.findViewById<TextView>(R.id.modeName)
        roomSettingRound = roomRemainTimeScreen.findViewById<TextView>(R.id.rounds_text)
        roomSettingRemainTime = roomRemainTimeScreen.findViewById<TextView>(R.id.timer_text)
        roomSettingLevel = roomRemainTimeScreen.findViewById<TextView>(R.id.levelName)
        roomTurnTopic = roomRemainTimeScreen.findViewById(R.id.tv_topic)

        viewLifecycleOwner.lifecycleScope.launch {
//        activity?.runOnUiThread {
            roomSettingMode
            roomSettingRound
            roomSettingRemainTime
            roomSettingLevel

            topic1 = topicScreen.findViewById<TextView>(R.id.topic_txt1)
            topic2 = topicScreen.findViewById<TextView>(R.id.topic_txt2)

            topic1.setOnClickListener {
                val json = JSONObject().apply {
                    put("event", "topicselect")
                    put("roomId", roomId)
                    put("topic", topic1.text)
                }
                topicScreen.visibility = View.GONE
                if (isMyTurn) {
                    binding.drawingView.isEnabled = true
                }
//                topic1.visibility = View.GONE
//                topic2.visibility = View.GONE
                GameWebSocketManager.getInstance().getWebSocket()?.send(json.toString())
            }
            topic2.setOnClickListener {
                val json = JSONObject().apply {
                    put("event", "topicselect")
                    put("roomId", roomId)
                    put("topic", topic2.text)
                }
                topicScreen.visibility = View.GONE
                if (isMyTurn) {
                    binding.drawingView.isEnabled = true
                }
//                topic1.visibility = View.GONE
//                topic2.visibility = View.GONE
                GameWebSocketManager.getInstance().getWebSocket()?.send(json.toString())
            }

        }


        // 초기 UI 업데이트
        updateUIForUserRole()

        // JS
        // 방 정보 조회 부분 수정
        settingRoominfo()

        // JS
        // 기존 유저 리스트 수신
        GameWebSocketManager.getInstance().addExistingRoomUserListeners { json ->
            // hostId는 이미 Fragment 생성 시 전달받았으므로 여기서 갱신하지 않음
            val users = json.optJSONArray("users")
            viewLifecycleOwner.lifecycleScope.launch {
//            activity?.runOnUiThread {
                updateUIForUserRole()
            }
        }
        // JS
        // 새로운 유저 입장 처리
        GameWebSocketManager.getInstance().addJoinListeners { json ->
            val joinedUserId = json.optInt("userId", -1)
            val joinedRoomId = json.optInt("roomId", -1)
            Log.d(TAG, "jojojojojo: $joinedUserId")
            if (joinedRoomId == roomId) {
                // 새로운 유저의 ID만 갱신하고 hostId는 유지
                if (joinedUserId == myUserId) {
                    viewLifecycleOwner.lifecycleScope.launch {
//                    activity?.runOnUiThread {
                        updateUIForUserRole()
                    }
                }
                viewLifecycleOwner.lifecycleScope.launch {
//                activity?.runOnUiThread {
                    var gameUserInfo = GameUserInfo(joinedUserId, 0, false, false)
                    roomUserAdapter.addRoomUser(
                        gameUserInfo,
                        binding.roomUserRecyclerView
                    )
                }
            }
        }

        // hostChange 이벤트 리스너 추가
        GameWebSocketManager.getInstance().addHostChangeListener { json ->
            val newHostId = json.optInt("userId", -1)
            if (newHostId != -1) {
                viewLifecycleOwner.lifecycleScope.launch {
                    hostId = newHostId  // hostId 업데이트
                    if (hostId == myUserId) {
                        binding.ivRoomManager.visibility = View.VISIBLE
                    } else {
                        binding.ivRoomManager.visibility = View.GONE
                    }
                    roomUserAdapter.updateItemByHostId(hostId)
                    if (!isStartGame) {
                        updateUIForUserRole()  // UI 업데이트
                    }
                }
            }
        }

        // 집 와서 한거
        GameWebSocketManager.getInstance().addGameCanStartListeners { json ->
            val roomId = json.optInt("roomId", -1)
            if (roomId == this.roomId) {
                viewLifecycleOwner.lifecycleScope.launch {
//                activity?.runOnUiThread {
                    val waitingText = waitingScreen.findViewById<TextView>(R.id.waiting_text)
                    isCanStartGame = true
                    waitingText.isEnabled = true
                }
            }
        }

        GameWebSocketManager.getInstance().addGameCantStartListeners { json ->
            val getRoomId = json.optInt("roomId", -1)
            viewLifecycleOwner.lifecycleScope.launch {
//            activity?.runOnUiThread {
                val waitingText = waitingScreen.findViewById<TextView>(R.id.waiting_text)
                if (getRoomId == roomId) {
                    isCanStartGame = false
                    waitingText.isEnabled = false
                }
            }
        }

        // 현재 topic 이라는 이벤트가 안날아오고 있음 백엔드 추가해야함
        GameWebSocketManager.getInstance().addTopicListeners { json ->
            val topicList = json.optJSONArray("topic")
            val nickname = json.optString("nickname")
            viewLifecycleOwner.lifecycleScope.launch {
//            activity?.runOnUiThread {
                val topic1 = topicScreen.findViewById<TextView>(R.id.topic_txt1)
                val topic2 = topicScreen.findViewById<TextView>(R.id.topic_txt2)

                topicList?.let { list ->
                    if (list.length() > 1) {
                        topic1.text = list.optString(0)
                        topic2.text = list.optString(1)
                    }
                }

                roomTurnTopic.text = "${nickname}님이 단어 고르는중..."
                roomTurnTopic.visibility = View.VISIBLE
                if (isMyTurn) {
                    topicScreen.visibility = View.VISIBLE
                    topic1.visibility = View.VISIBLE
                    topic2.visibility = View.VISIBLE
                }
                binding.drawingView.clearDrawing()
            }
        }
        GameWebSocketManager.getInstance().addRemainTimeListeners { json ->
            val Time = json.optInt("remaintime", -1)
            viewLifecycleOwner.lifecycleScope.launch {
//            activity?.runOnUiThread {
//                remainTime = topicScreen.findViewById<TextView>(R.id.remaining_time)
//                remainTime.text = Time.toString()
                roomSettingRemainTime.text = Time.toString()

                if (roomSettingRemainTime.text.equals("0")) {
//                    remainTime.visibility = View.GONE
                    topicScreen.visibility = View.GONE
//                    if (isMyTurn) {
//                        binding.drawingView.isEnabled = true
//                    }
                } else {
                    if (isMyTurn) {
//                        topicScreen.visibility = View.VISIBLE
//                        topic1.visibility = View.VISIBLE
//                        topic2.visibility = View.VISIBLE
                    } else {
                        topicScreen.visibility = View.GONE
//                        topic1.visibility = View.GONE
//                        topic2.visibility = View.GONE
                    }
//                    remainTime.visibility = View.VISIBLE
//                    binding.drawingView.isEnabled = false
                }
            }
        }

        //현재 start 따로 안 날아오고 있음
        GameWebSocketManager.getInstance().addStartListeners { json ->
            val getRoomId = json.optInt("roomId", -1)
            viewLifecycleOwner.lifecycleScope.launch {
//            activity?.runOnUiThread {
                if (getRoomId == roomId) {
                    waitingScreen.visibility = View.GONE
                    isStartGame = true
                }
            }
        }

        GameWebSocketManager.getInstance().addTopicSelectListeners { json ->
            val getRoomId = json.optInt("roomId", -1)
            val topicSelceted = json.optString("topic", "....")
            val nickname = json.optString("nickname", "닉네임 오류...")
            viewLifecycleOwner.lifecycleScope.launch {
//            activity?.runOnUiThread {
                if (getRoomId == roomId) {
                    // 이거 뭐 안해줘도 될꺼 같은데?
                    topicScreen.visibility = View.GONE
//                    topic1.visibility = View.GONE
//                    topic2.visibility = View.GONE
                    if (isMyTurn) {
                        binding.drawingView.isEnabled = true
                        roomTurnTopic.text = topicSelceted
                    } else {
                        binding.drawingView.isEnabled = false
                        roomTurnTopic.text = "${nickname}님이 그림그리는 중..."
                    }
                }
            }
        }

        GameWebSocketManager.getInstance().addCurrentRoundListeners { json ->
            // 필요한 경우에만 구현
        }

//        GameWebSocketManager.getInstance().addTopicSelectedListeners { json ->
//            val roomId = json.optInt("roomId", -1)
//            activity?.runOnUiThread {
//                // 이거도 따로 안해도 될꺼 같은데?
//            }
//        }

        GameWebSocketManager.getInstance().addStrokeChangeListeners { json ->
            val getRoomId = json.optInt("roomId", -1)
            val stroke = json.optInt("stroke", -1)
            val userId = json.optInt("userId", -1)
            Log.d("ㄴㅇㄴㅇㄴ", "시크바 stroke : ${stroke}")
            viewLifecycleOwner.lifecycleScope.launch {
                if (getRoomId == roomId && myUserId != userId) {
                    binding.verticalSeekBar.setProgress(stroke)
                    Log.d("ㄴㅇㄴㅇㄴ", "시크바 stroke : ${binding.verticalSeekBar.progress.toFloat()}")
                    binding.drawingView.drawPaint.strokeWidth =
                        binding.verticalSeekBar.progress.toFloat()
                    binding.drawingView.erasePaint.strokeWidth =
                        binding.verticalSeekBar.progress.toFloat()
                    Log.d("ㄴㅇㄴㅇㄴ", "시크바 stroke : ${binding.drawingView.drawPaint.strokeWidth}")
                }
            }
        }

        binding.constraintLayout.setBackgroundResource(R.drawable.round_backgroud_border_black)
        GameWebSocketManager.getInstance().addCorrectListeners { json ->
            val roomId = json.optInt("roomId", -1)
            val userId = json.optInt("userId", -1)
            if (this.roomId == roomId && myUserId == userId) {
                var nickname = binding.gameMyNickName.text.toString()
                var message = nickname + "님이 정답을 맞추셨습니다!"
                val json = JSONObject().apply {
                    put("event", "correctchat")
                    put("roomId", roomId)
                    put("message", message)
                }
                GameWebSocketManager.getInstance().getWebSocket()?.send(json.toString())
                binding.constraintLayout.background = ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.round_background_border_green
                )
            }
        }

        GameWebSocketManager.getInstance().addNextRoundListeners { json ->
            val userIdTurn = json.optInt("turn", -1)
            val round = json.optInt("round", -1)
            if (roomSettingMode.text == "USER") {
                viewLifecycleOwner.lifecycleScope.launch {
//                activity?.runOnUiThread {
                    binding.aiImageView.visibility = View.GONE

                    // 연필 초기화
                    sendColorChangeMessage("#FF000000")
                    binding.drawingView.drawPaint.color = currentColor
                    binding.drawingView.eraserEnabled = false
                    binding.verticalSeekBar.setProgress(10)
                    binding.drawingView.drawPaint.strokeWidth =
                        binding.verticalSeekBar.progress.toFloat()
                    binding.drawingView.erasePaint.strokeWidth =
                        binding.verticalSeekBar.progress.toFloat()
                    clearcolor()


                    if (myUserId == userIdTurn) {
                        isMyTurn = true
                        binding.roomUserBox.visibility = View.GONE
                        binding.drawBox.visibility = View.VISIBLE
                        binding.startButton.visibility = View.GONE
                        binding.answer.visibility = View.GONE

//                    binding.drawingView.isEnabled = true
                        topicScreen.visibility = View.VISIBLE
                    } else {
                        isMyTurn = false
                        binding.roomUserBox.visibility = View.VISIBLE
                        binding.drawBox.visibility = View.GONE
                        binding.startButton.visibility = View.VISIBLE
                        binding.answer.visibility = View.VISIBLE

                        binding.drawingView.isEnabled = false
                        topicScreen.visibility = View.GONE
                    }
                    binding.constraintLayout.background = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.round_backgroud_border_black
                    )
                    roomUserAdapter.userIdList.forEach {
                        roomUserAdapter.updateItemByUserId(it.userId, 0, 1, false)
                    }
                    roomSettingRound.text = round.toString() + " / " + myRoomInfo.rounds.toString()
                }
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    binding.aiImageView.visibility = View.VISIBLE
                    binding.drawingView.isEnabled = false
                    binding.drawBox.visibility = View.GONE
                    isMyTurn = false
                    topicScreen.visibility = View.GONE

                    // 몇 라운드 인지 보여주기랑 맞춘사람 표시 초기화
                    binding.constraintLayout.background = ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.round_backgroud_border_black
                    )
                    roomUserAdapter.userIdList.forEach {
                        roomUserAdapter.updateItemByUserId(it.userId, 0, 1, false)
                    }
                    roomSettingRound.text = round.toString() + " / " + myRoomInfo.rounds.toString()

                    // 시간초 초기화
                    roomSettingRemainTime.text = myRoomInfo.roundTime.toString()

                    // ai 이미지 초기화
                    Glide.with(binding.root.context).clear(binding.aiImageView)
                    binding.aiImageView.setImageDrawable(null)

                    // AI 그리는 중 텍스트 표시
                    binding.aiDrawingText.visibility = View.VISIBLE
                    binding.aiDrawingText.text = "AI가 그리는 중..."

                    // ai 이미지 로딩중
                    Glide.with(binding.root.context)
                        .load(R.raw.ai_drawing2)
                        .into(binding.aiImageView)

                }
            }
        }

        GameWebSocketManager.getInstance().addAiDrawingListeners { json ->
            val aiDrawing = json.optString("images", "null???")
            viewLifecycleOwner.lifecycleScope.launch {

                binding.aiDrawingText.visibility = View.GONE

                // ai 이미지 넣기
                Glide.with(binding.root.context)
                    .load(aiDrawing)
                    .into(binding.aiImageView)
            }
        }

        // ysw
        GameWebSocketManager.getInstance().addScoreListeners { json ->
            val score = json.optInt("score", -1)
            val userId = json.optInt("userId", -1)
            viewLifecycleOwner.lifecycleScope.launch {
//            activity?.runOnUiThread {
                roomUserAdapter.updateItemByUserId(userId, score, 1, true)
                if (userId == myUserId) {
                    myScore += score
                    binding.userScore.text = myScore.toString() + "pt"
                }
            }
        }

        GameWebSocketManager.getInstance().addWinnerListeners { json ->
            val userId = json.optInt("userId", -1)
            val winnerScore = json.optInt("score", -1)
            viewLifecycleOwner.lifecycleScope.launch {
                val userInfo = RetrofitUtil.userService.getUserInfo(userId)
                currentGameWinnerUserId = userId
                // 이제 currentGameWinnerUserId를 화면에 띄워주기
                if (currentGameWinnerUserId != -1) {
                    val gameWinnerFragment = GameWinnerFragment().apply {
                        arguments = Bundle().apply {
                            putString("nickname", userInfo.nickname)
                            putInt("score", winnerScore)
                        }
                    }
                    gameWinnerFragment.show(
                        parentFragmentManager,
                        "GameWinnerFragment"
                    )
                }
            }
        }

        GameWebSocketManager.getInstance().addChangeRoomInfoListeners { json ->
            settingRoominfo()
        }

        GameWebSocketManager.getInstance().addEndGameListeners { json ->
            val roomId = json.optInt("roomId", -1)
            viewLifecycleOwner.lifecycleScope.launch { // lifecycleScope 사용
                if (_binding != null) { // binding null 체크
                    activity?.runOnUiThread {
                        if (this@PlayGameFragment.roomId == roomId) {
                            setInitialState()
                        }
                    }
                }
            }
        }

        GameWebSocketManager.getInstance().addjoinMessageListeners { json ->
            viewLifecycleOwner.lifecycleScope.launch { // lifecycleScope 사용
                if (_binding != null) { // binding null 체크
                    activity?.runOnUiThread {
                        val message = json.optString("message", "")
                        wordAdapter.addBlueWord(
                            message,
                            "입장",
                            binding.wordRecyclerView,
                            binding.root.context
                        )
                    }
                }
            }
        }

        GameWebSocketManager.getInstance().addleaveMessageListeners { json ->
            viewLifecycleOwner.lifecycleScope.launch { // lifecycleScope 사용
                if (_binding != null) { // binding null 체크
                    activity?.runOnUiThread {
                        val message = json.optString("message", "")
                        wordAdapter.addRedWord(
                            message,
                            "퇴장",
                            binding.wordRecyclerView,
                            binding.root.context
                        )
                    }
                }
            }
        }

        GameWebSocketManager.getInstance().addcorrectMessageListeners { json ->
            viewLifecycleOwner.lifecycleScope.launch { // lifecycleScope 사용
                if (_binding != null) { // binding null 체크
                    activity?.runOnUiThread {
                        val message = json.optString("message", "")
                        wordAdapter.addGreenWord(
                            message,
                            binding.wordRecyclerView,
                            binding.root.context
                        )
                    }
                }
            }
        }

        GameWebSocketManager.getInstance().addanswerMessageListeners { json ->
            viewLifecycleOwner.lifecycleScope.launch { // lifecycleScope 사용
                if (_binding != null) { // binding null 체크
                    activity?.runOnUiThread {
                        val message = json.optString("message", "")
                        wordAdapter.addAnswerWord(
                            message,
                            binding.wordRecyclerView,
                            binding.root.context
                        )
                    }
                }
            }
        }

        // 그림 저장 버튼 클릭 리스너 추가
        binding.saveDrawingButtonJw.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 이상에서는 권한 체크 없이 바로 저장
                saveDrawing()
            } else {
                // Android 13 미만에서는 저장소 권한 체크
                when {
                    checkStoragePermission() -> {
                        saveDrawing()
                    }

                    else -> {
                        requestStoragePermission()
                    }
                }
            }
        }
    }

    override fun onConfirmExit() {
        try {
            val user = ApplicationClass.sharedPreferencesUtil.getUser()
            val webSocket = GameWebSocketManager.getInstance().getWebSocket()

            val json = JSONObject().apply {
                put("event", "leave")
                put("userId", user.userId)
                put("roomId", roomId)
            }
            webSocket?.send(json.toString())

            // 게임 음악 중지하고 메인 음악으로 전환
            (requireActivity() as MainActivity).stopGameMusic()

            // 나가면서 데이터 베이스 불러오기
            viewModel.userInfo(user.userId)

            // LobbyFragment로 전환
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, LobbyFragment())
                .commit()
        } catch (e: Exception) {
            Log.e(TAG, "Error leaving room", e)
        }
    }

    // 게임 끝날때 방 상태 처음으로 돌리고 게임 끝날때 보낼것 들
    private fun setInitialState() {
        isStartGame = false
        // ai 이미지 초기화
        Glide.with(binding.root.context).clear(binding.aiImageView)
        binding.aiImageView.setImageDrawable(null)
        // AI 화면 안보이게 초기화
        binding.aiImageView.visibility = View.GONE

        // 연필 초기화
        sendColorChangeMessage("#FF000000")
        binding.drawingView.drawPaint.color = currentColor
        binding.drawingView.eraserEnabled = false
        binding.verticalSeekBar.setProgress(10)
        binding.drawingView.drawPaint.strokeWidth =
            binding.verticalSeekBar.progress.toFloat()
        binding.drawingView.erasePaint.strokeWidth =
            binding.verticalSeekBar.progress.toFloat()
        clearcolor()

        binding.drawingView.sendClearDrawing()
        topicScreen.visibility = View.GONE
        updateUIForUserRole()
        binding.drawingView.isEnabled = false
        binding.roomUserBox.visibility = View.VISIBLE
        binding.drawBox.visibility = View.GONE
        binding.startButton.visibility = View.VISIBLE
        binding.answer.visibility = View.VISIBLE

        roomSettingMode.text = myRoomInfo.mode.toString()
        roomSettingRound.text = myRoomInfo.rounds.toString()
        roomSettingRemainTime.text = myRoomInfo.roundTime.toString()
        roomSettingLevel.text = myRoomInfo.level.toString()
        // 주제 숨김
        roomTurnTopic.visibility = View.GONE


        val json = JSONObject().apply {
            put("event", "resultscore")
            put("roomId", roomId)
            put("userId", myUserId)
            put("score", myScore)
        }
        GameWebSocketManager.getInstance().sendMessage(json.toString())
        myScore = 0
        binding.userScore.text = myScore.toString() + "pt"

        roomUserAdapter.userIdList.forEach { gameUserInfo ->
            roomUserAdapter.updateItemByUserId(gameUserInfo.userId, 0, 0, false)
        }
        binding.constraintLayout.background = ContextCompat.getDrawable(
            binding.root.context,
            R.drawable.round_backgroud_border_black
        )
    }

    // JS
    // 대기 화면 텍스트 변환기 수정
    private fun updateUIForUserRole() {
        val waitingText = waitingScreen.findViewById<TextView>(R.id.waiting_text)
        val settingButton = waitingScreen.findViewById<ImageButton>(R.id.setting_button)

        if (myUserId == hostId) {
            waitingText.text = "시작하기"
            settingButton.visibility = View.VISIBLE
            waitingScreen.visibility = View.VISIBLE
            // 방장용 클릭 리스너 설정
            if (isCanStartGame) {
                waitingText.isEnabled = true
            } else {
                waitingText.isEnabled = false
            }
            waitingText.setOnClickListener {
                // 게임 시작 로직
                val json = JSONObject().apply {
                    put("event", "start")
                    put("roomId", roomId)
                }
                GameWebSocketManager.getInstance().getWebSocket()?.send(json.toString())
                waitingScreen.visibility = View.GONE
            }

            // 설정 버튼 클릭 리스너 추가
            settingButton.setOnClickListener {
                val gameSettingFragment = GameSettingFragment().apply {
                    arguments = Bundle().apply {
                        putInt("roomId", roomId)
                    }
                }
                gameSettingFragment.show(parentFragmentManager, "GameSettingFragment")
            }

        } else {
            waitingScreen.visibility = View.VISIBLE
            waitingText.text = "대기중..."
            settingButton.visibility = View.GONE
            waitingText.setOnClickListener(null)  // 클릭 리스너 제거
            settingButton.setOnClickListener(null)  // 클릭 리스너 제거
        }
    }

    private fun setupColorButton(imageView: ImageView, colorResId: Int, colorHex: String) {
        imageView.setOnClickListener {
            binding.apply {
                if (!binding.drawingView.eraserEnabled) {
                    currentColor = ContextCompat.getColor(requireContext(), colorResId)
                    drawingView.drawPaint.color = currentColor
                    sendColorChangeMessage(colorHex)

                    // 모든 색상 버튼의 테두리를 제거
                    penColorRed.foreground = null
                    penColorOrange.foreground = null
                    penColorYellow.foreground = null
                    penColorGreen.foreground = null
                    penColorBlue.foreground = null
                    penColorMint.foreground = null
                    penColorPurple.foreground = null
                    penColorBlack.foreground = null

                    // 선택된 색상 버튼에 테두리 추가
                    imageView.foreground = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.selected_color_button
                    )
                }
            }
        }
    }


    //연필 크기 바꾸는 함수
    private fun setPencilSize(size: Float) {
        binding.drawingView.drawPaint.strokeWidth = size
        binding.drawingView.erasePaint.strokeWidth = size
    }

    // 메시지 전송
    private fun sendMessage(message: String) {
        val user = ApplicationClass.sharedPreferencesUtil.getUser()
        val json = JSONObject().apply {
            put("event", "chat")
            put("message", message)
            put("userId", user.userId)
            put("roomId", roomId)
            put("nickname", binding.gameMyNickName.text)
        }
        GameWebSocketManager.getInstance().sendMessage(json.toString())
    }

    // Fragment에서 색상 변경 시 호출
    private fun sendColorChangeMessage(color: String) {
        val json = JSONObject().apply {
            put("event", "colorchange")
            put("color", color)
            put("roomId", roomId)
        }
        GameWebSocketManager.getInstance().sendMessage(json.toString())
    }

    // DrawingView에 접근할 수 있는 메서드 추가
    fun getDrawingView() = _binding?.drawingView

    override fun onDestroyView() {
        super.onDestroyView()
        GameWebSocketManager.getInstance().clearExistingRoomUserListeners()
        GameWebSocketManager.getInstance().clearJoinListeners()
        GameWebSocketManager.getInstance().clearDrawingEventListeners()
        GameWebSocketManager.getInstance().clearMessageEventListeners()
        GameWebSocketManager.getInstance().clearHostChangeListeners()  // hostChange 리스너 제거

        // 집 와서 한거
        GameWebSocketManager.getInstance().clearStartListeners()
        GameWebSocketManager.getInstance().clearTopicSelectListeners()
        GameWebSocketManager.getInstance().clearRemainTimeListeners()
        GameWebSocketManager.getInstance().clearCurrentRoundListeners()
        GameWebSocketManager.getInstance().clearTopicListeners()
//        GameWebSocketManager.getInstance().clearTopicSelectedListeners()
        GameWebSocketManager.getInstance().clearCorrectListeners()
        GameWebSocketManager.getInstance().clearNextRoundListeners()
        GameWebSocketManager.getInstance().clearEndGameListeners()
        GameWebSocketManager.getInstance().clearGameCanStartListeners()
        GameWebSocketManager.getInstance().clearGameCantStartListeners()
        GameWebSocketManager.getInstance().clearChangeRoomInfoListeners()
        GameWebSocketManager.getInstance().clearleaveMessageListeners()
        GameWebSocketManager.getInstance().clearjoinMessageListeners()
        GameWebSocketManager.getInstance().clearcorrectMessageListeners()
        GameWebSocketManager.getInstance().clearAiDrawingListeners()
        GameWebSocketManager.getInstance().clearanswerMessageListeners()
        GameWebSocketManager.getInstance().clearStrokeChangeListeners()
        _binding = null
    }

    // DrawingView 클래스에 비트맵 변환 메서드 추가
    fun DrawingView.getBitmap(): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        draw(canvas)
        return returnedBitmap
    }

    // 권한 체크 함수 수정
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true  // Android 13 이상에서는 앱 전용 저장소에 대한 권한이 필요 없음
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 권한 요청 함수 수정
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상에서는 앱 전용 저장소에 바로 저장
            saveDrawing()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    saveDrawing()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    Toast.makeText(context, "그림을 저장하기 위해 저장소 권한이 필요합니다", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    // 저장 로직을 별도 함수로 분리
    private fun saveDrawing() {
        val drawingView = binding.drawingView
        val bitmap = drawingView.getBitmap()

        lifecycleScope.launch {
            try {
                // 비트맵을 파일로 변환
                val file = createTempFileFromBitmap(bitmap)

                // MultipartBody.Part 생성
                val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // 현재 게임의 주제어 가져오기
                val topic = getCurrentTopic()

                var response = RetrofitUtil.pictureUploadService.uploadPicture(
                    userId = ApplicationClass.sharedPreferencesUtil.getUser().userId,
                    topic = topic,
                    file = body
                )
                // 서버로 업로드
                if (response.isSuccessful) {
                    Toast.makeText(context, "그림이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "서버 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(context, "저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("saveDrawing", "catch: ${e.printStackTrace()}")
            }
        }
    }

    private fun createTempFileFromBitmap(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "temp_drawing_${System.currentTimeMillis()}.png")
        file.createNewFile()

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        return file
    }

    private fun saveDrawingLocally(bitmap: Bitmap) {
        val imagesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!imagesDir?.exists()!!) {
            imagesDir.mkdirs()
        }

        val fileName = "Drawing_${System.currentTimeMillis()}.png"
        val file = File(imagesDir, fileName)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    private fun settingRoominfo() {
        lifecycleScope.launch {
            try {
                if (roomId != -1) {  // roomId가 유효한 경우에만 실행
                    viewModel.getOneRoomInfo(roomId) { roomInfo ->
                        activity?.runOnUiThread {
                            roomInfo?.let { nonNullRoomInfo ->
                                myRoomInfo = nonNullRoomInfo
                                when (nonNullRoomInfo.status) {
                                    "WAIT" -> {
                                        binding.drawingView.isEnabled = false
                                        binding.waitingFrame.visibility = View.VISIBLE
                                        waitingScreen.visibility = View.VISIBLE
                                        updateUIForUserRole()
                                    }

                                    else -> {
                                        binding.drawingView.isEnabled = true
                                        binding.waitingFrame.visibility = View.GONE
                                        waitingScreen.visibility = View.GONE
                                    }
                                }
                                binding.gameRoomName.text = nonNullRoomInfo.roomName
                                roomSettingMode.text = nonNullRoomInfo.mode.toString()
                                roomSettingRound.text = nonNullRoomInfo.rounds.toString()
                                roomSettingRemainTime.text = nonNullRoomInfo.roundTime.toString()
                                roomSettingLevel.text = nonNullRoomInfo.level.toString()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching room info", e)
            }
        }
    }
fun clearcolor(){
    binding.penColorRed.foreground = null
    binding.penColorBlue.foreground = null
    binding.penColorMint.foreground = null
    binding.penColorOrange.foreground = null
    binding.penColorPurple.foreground = null
    binding.penColorYellow.foreground = null
    binding.penColorGreen.foreground = null
    binding.penColorBlack.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.selected_color_button)
    binding.pen.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
    binding.eraser.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white)) // 기본 배경색
}
    //    private suspend fun loadImage(url: String): Bitmap? {
//        return withContext(Dispatchers.IO) {
//            Glide.with(binding.root.context)
//                .asBitmap()
//                .load(url)
//                .placeholder(R.drawable.user_profile)
//                .error()
//                .submit()
//                .get()
//        }
//    }
//
//    private fun drawOnCanvas(bitmap: Bitmap?) {
//        bitmap?.let {
//            val canvasBitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
//            val canvas = Canvas(canvasBitmap)
//            canvas.drawBitmap(it, 0f, 0f, null)
//
//            val imageView = view?.findViewById<ImageView>(R.id.drawingView)
//            imageView?.setImageBitmap(canvasBitmap)
//        }
//    }
    private fun getCurrentTopic(): String {
        return roomTurnTopic.text.toString().replace("님이 그림그리는 중...", "")
    }
}