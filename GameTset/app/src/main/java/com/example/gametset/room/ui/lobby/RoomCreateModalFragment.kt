package com.example.gametset.room.ui.lobby

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.gametset.R
import com.example.gametset.databinding.FragmentRoomCreateModalBinding
import com.example.gametset.databinding.FragmentStoreModalBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.base.ApplicationClass.Companion.sharedPreferencesUtil
import com.example.gametset.room.data.model.dto.RoomCreateDto
import com.example.gametset.room.ui.gameRoom.PlayGameFragment
import com.example.gametset.room.websocket.GameWebSocketManager
import org.json.JSONObject
import android.content.Context

class RoomCreateModalFragment : DialogFragment() {

    lateinit var mainActivity: MainActivity
    private var _binding: FragmentRoomCreateModalBinding? = null
    private val binding get() = _binding!!
    private val viewModel : MainActivityViewModel by activityViewModels()
    private var currentMaxPlayers = 4
    private var currentRound = 3
    private var currentRoundTime = 30
    private var currentLevel = "EASY"
    private var currentMode = "USER"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomCreateModalBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentLevel = "EASY"

        setupModeSeekBar()
        setupTimeSeekBar()
        setupRoundSeekBar()
        setupPlayerSeekBar()
        setSelectedButton()

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

        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.createButton.setOnClickListener {
            createPlayRoom()
        }
    }

    private fun setSelectedButton() {
        binding.diffEasyButton.isSelected = false
        binding.diffNormalButton.isSelected = false
        binding.diffHardButton.isSelected = false

        if(currentLevel == "EASY"){
            binding.diffEasyButton.isSelected = true
        }else if(currentLevel == "NORMAL"){
            binding.diffNormalButton.isSelected = true
        }else if(currentLevel == "HARD"){
            binding.diffHardButton.isSelected = true
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

    private fun createPlayRoom() {
        var roomName = binding.createRoomTitle.text.toString()
        val user = sharedPreferencesUtil.getUser()

        if(roomName.isEmpty()){
            roomName="제목이 없습니다!"
        }

        if (roomName.isNotEmpty()) {
            val roomCreateDto = RoomCreateDto(
                hostId = user.userId,
                roomName = roomName,
                status = "WAIT",
                maxPlayers = currentMaxPlayers,
                roomId = 0,
                mode = currentMode,
                roundTime = currentRoundTime,
                rounds = currentRound,
                level = currentLevel
            )

            viewModel.createRoom(roomCreateDto) { roomId ->
                if (roomId > 0) {
                    val webSocket = GameWebSocketManager.getInstance().getWebSocket()
                    val json = JSONObject().apply {
                        put("event", "join")
                        put("userId", user.userId)
                        put("roomId", roomId)
                    }
                    webSocket?.send(json.toString())

                    GameWebSocketManager.getInstance().addExistingRoomUserListeners { response ->
                        val hostId = response.optString("hostId", "-1").toInt()
                        val userArray = response.getJSONArray("users")
                        activity?.runOnUiThread {
                            mainActivity.startGameMusic()
                            dismiss()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.frame_layout_main, PlayGameFragment.newInstance(roomId, hostId, userArray))
                                .commit()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}