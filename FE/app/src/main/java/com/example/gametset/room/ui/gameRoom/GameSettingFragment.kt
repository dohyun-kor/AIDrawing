package com.example.gametset.room.ui.gameRoom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.gametset.databinding.FragmentGameSettingBinding
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.data.model.dto.RoomChangeDto
import com.example.gametset.room.websocket.GameWebSocketManager
import org.json.JSONObject


class GameSettingFragment : DialogFragment() {

    private var _binding: FragmentGameSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainActivityViewModel by activityViewModels()
    private var currentMaxPlayers = 4
    private var currentRound = 3
    private var currentRoundTime = 30
    private var currentRoomTitle = ""
    private var currentMode = "USER"
    private var currentLevel = ""   // 기본값 설정
    private var currentStatus = ""
    private var currentHostId = -1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimeSeekBar()
        setupRoundSeekBar()
        setupPlayerSeekBar()
        setupModeSeekBar()

        // 현재 방 정보 가져오기
        val roomId = arguments?.getInt("roomId") ?: return
        loadRoomInfo(roomId)

        binding.diffEasyButton.setOnClickListener {
            currentLevel = "EASY"
            setSelectedButton()
        }

        binding.diffNormalButton.setOnClickListener {
            currentLevel = "NORMAL"
            setSelectedButton()
        }

        binding.diffHardButton.setOnClickListener {
            currentLevel = "HARD"
            setSelectedButton()
        }

        binding.dialogLayout.setOnClickListener {
            dismiss()
        }

        binding.CreateBody.setOnClickListener {
            // 빈 클릭 리스너
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.changeButton.setOnClickListener {
            changeRoom(roomId)
        }
    }

    private fun setSelectedButton() {
        binding.diffEasyButton.isSelected = false
        binding.diffNormalButton.isSelected = false
        binding.diffHardButton.isSelected = false

        if (currentLevel == "EASY") {
            binding.diffEasyButton.isSelected = true
        } else if (currentLevel == "NORMAL") {
            binding.diffNormalButton.isSelected = true
        } else if (currentLevel == "HARD") {
            binding.diffHardButton.isSelected = true
        }
    }

    private fun loadRoomInfo(roomId: Int) {
        viewModel.getOneRoomInfo(roomId) { room ->
            room?.let {
                currentRoomTitle = it.roomName
                currentMode = it.mode
                currentLevel = it.level
                currentMaxPlayers = it.maxPlayers
                currentRound = it.rounds
                currentRoundTime = it.roundTime
                currentStatus = it.status
                currentHostId = it.hostId

                // UI 업데이트
                binding.RoomTitle.setText(currentRoomTitle)

                // SeekBar 초기값 설정
                updateSeekBarsToCurrentValues()
                setSelectedButton()
            }
        }
    }

    private fun setupModeSeekBar() {
        binding.modeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentMode = if (progress == 0) "USER" else "AI"
                binding.roomModeTitle.text = currentMode
                seekBar?.isSelected = progress > 0
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateSeekBarsToCurrentValues() {
        // 시간 SeekBar 설정
        val timeArray = arrayOf(30, 40, 50, 60)
        val timeIndex = timeArray.indexOf(currentRoundTime).coerceAtLeast(0)
        binding.seekBar.progress = timeIndex

        // 라운드 SeekBar 설정
        val roundArray = arrayOf(3, 6, 9, 12)
        val roundIndex = roundArray.indexOf(currentRound).coerceAtLeast(0)
        binding.roundSeekBar.progress = roundIndex

        // 플레이어 SeekBar 설정
        val playerArray = arrayOf(4, 6, 8)
        val playerIndex = playerArray.indexOf(currentMaxPlayers).coerceAtLeast(0)
        binding.playerSeekBar.progress = playerIndex

        // 모드 SeekBar 설정

        binding.modeSeekBar.progress = if (currentMode == "AI") 1 else 0
        binding.roomModeTitle.text = currentMode
    }

    private fun changeRoom(roomId: Int) {
        var newTitle = binding.RoomTitle.text.toString()

        if (newTitle.isEmpty()) {
            newTitle = "제목이 없습니다!"
        }

        val roomChangeDto = RoomChangeDto(
            roomId = roomId,
            hostId = currentHostId,
            roomName = newTitle,
            status = currentStatus,
            maxPlayers = currentMaxPlayers,
            rounds = currentRound,
            mode = currentMode,
            level = currentLevel,
            roundTime = currentRoundTime
        )

        // REST API로 방 정보 업데이트
        viewModel.putOneRoomInfo(roomId, roomChangeDto) { success ->
            if (success) {
                viewModel.setRoomInfo(roomChangeDto)
                dismiss()
                // WebSocket으로 방 정보 변경 이벤트 전송
                val json = JSONObject().apply {
                    put("event", "changeroominfo")
                    put("roomId", roomId)
                }
                GameWebSocketManager.getInstance().getWebSocket()?.send(json.toString())
            }
        }
    }

    private fun setupTimeSeekBar() {
        val timeArray = arrayOf(30, 40, 50, 60)
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentRoundTime = timeArray[progress]
                binding.timeText.text = "${timeArray[progress]}초"
                seekBar?.isSelected = progress > 0
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupRoundSeekBar() {
        val roundArray = arrayOf(3, 6, 9, 12)
        binding.roundSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentRound = roundArray[progress]
                binding.roundText.text = "${roundArray[progress]} rounds"
                seekBar?.isSelected = progress > 0
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupPlayerSeekBar() {
        val playerArray = arrayOf(4, 6, 8)
        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentMaxPlayers = playerArray[progress]
                binding.playerText.text = "${playerArray[progress]}명"
                seekBar?.isSelected = progress > 0
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

}