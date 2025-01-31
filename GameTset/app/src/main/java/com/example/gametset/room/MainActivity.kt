package com.example.gametset.room

import android.graphics.Color
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gametset.R
import com.example.gametset.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

private const val TAG = "MainActivity_싸피"

// MainActivity 클래스 정의
class MainActivity : AppCompatActivity() {

    // 뷰 바인딩 객체
    private lateinit var binding: ActivityMainBinding

    // 시뮬레이션된 경로 목록
    private lateinit var simulatedPaths: List<Path>

    // WebSocket 객체
    private lateinit var webSocket: WebSocket

    // 어댑터 객체
    private lateinit var wordAdapter: WordAdapter

    // 단어 목록
    private val wordList = mutableListOf<String>()

    // 단어 목록
    private var currentColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wordAdapter = WordAdapter(wordList)
        binding.wordRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.wordRecyclerView.adapter = wordAdapter
        currentColor = ContextCompat.getColor(this@MainActivity, R.color.black)

        // WebSocket 연결 초기화
        setupWebSocketConnection()

        // DrawingView에 WebSocket 전달
        binding.drawingView.setWebSocket(webSocket)

        // 펜,지우개 모드
        binding.apply {
            pen.setOnClickListener {
                binding.drawingView.drawPaint.color = currentColor
                binding.drawingView.eraserEnabled = false
            }
            eraser.setOnClickListener {
                binding.drawingView.drawPaint.color =
                    ContextCompat.getColor(this@MainActivity, R.color.white)
                sendColorChangeMessage("#FFFFFF")
                binding.drawingView.eraserEnabled = true
            }
        }

        // Start 버튼 클릭 시 메시지 전송
        binding.startButton.setOnClickListener {
            val text = binding.answer.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(this, "입력창이 비었습니다!", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(text)
                wordAdapter.addWord(text, binding.wordRecyclerView)
            }
        }

        // Clear 버튼 클릭 시 그림판 지우기
        binding.clearButton.setOnClickListener {
            binding.drawingView.sendClearDrawing()
        }

        // 색상 선택 버튼 클릭 이벤트 처리
        binding.apply {
            setupColorButton(penColorRed, R.color.red, "#FF0000")
            setupColorButton(penColorOrange, R.color.orange, "#FF7F50")
            setupColorButton(penColorYellow, R.color.yellow, "#FFFF00")
            setupColorButton(penColorGreen, R.color.green, "#ADFF2F")
            setupColorButton(penColorBlue, R.color.blue, "#0000FF")
            setupColorButton(penColorNavyBlue, R.color.navy_blue, "#000080")
            setupColorButton(penColorPurple, R.color.purple, "#800080")
            setupColorButton(penColorBlack, R.color.black, "#FF000000")
        }

        binding.verticalSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                setPencilSize(progress.toFloat()) // SeekBar의 현재 값을 연필 크기로 설정
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // 사용자가 SeekBar를 터치하기 시작할 때 호출됩니다.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // 사용자가 SeekBar를 터치한 후 손을 뗄 때 호출됩니다.
            }
        })
    }

    // 연필 색 바꾸는 함수
    private fun setupColorButton(imageView: ImageView, colorResId: Int, colorHex: String) {
        imageView.setOnClickListener {
            binding.apply {
                // 펜 모드 일때
                if (!binding.drawingView.eraserEnabled) {
                    currentColor = ContextCompat.getColor(this@MainActivity, colorResId)
                    drawingView.drawPaint.color = currentColor
                    sendColorChangeMessage(colorHex)
                }
            }
        }
    }

    //연필 크기 바꾸는 함수
    private fun setPencilSize(size:Float){
        binding.drawingView.drawPaint.strokeWidth = size
        binding.drawingView.erasePaint.strokeWidth = size
    }

    // WebSocket 연결 설정
    private fun setupWebSocketConnection() {
//        val serverUrl = "ws://192.168.100.203:9987/ws"
        val serverUrl = "wss://i12d108.p.ssafy.io/api/ws"
        val client = OkHttpClient()
        val request = Request.Builder().url(serverUrl).build()

        // WebSocket 리스너 설정
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                runOnUiThread {
                    try {
                        val json = JSONObject(text)
                        val event = json.optString("event", "")

                        when (event) {
                            "draw" -> {
                                val x = json.optDouble("x", -1.0).toFloat()
                                val y = json.optDouble("y", -1.0).toFloat()
                                val color = json.optString("color", "#000000")
                                val mode = json.optInt("mode", 1)
                                if (x >= 0 && y >= 0) {
                                    binding.drawingView.drawFromServer(x, y, color, mode)
                                }
                            }

                            "colorChange" -> {
                                val color = json.optString("color", "#000000")
                                binding.drawingView.updateColorFromServer(color)
                            }

                            "clearDrawing" -> binding.drawingView.clearDrawing()
                            else -> wordAdapter.addWord(text, binding.wordRecyclerView)
                        }

                    } catch (e: Exception) {
                        Log.e(TAG, "🚨 WebSocket 메시지 파싱 오류: ${e.message}")
                    }
                }
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: okhttp3.Response?
            ) {
                super.onFailure(webSocket, t, response)
                runOnUiThread {
                    Log.d(TAG, "onFailure: ${t.message}")
                    Log.d(TAG, "Response: ${response?.code} and ${response?.isSuccessful}")

                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)
                sendMessage("Hello from Android!")
            }
        })
    }

    // 메시지 전송
    private fun sendMessage(message: String) {
        webSocket.send(message)
    }

    // MainActivity에서 색상 변경 시 호출
    private fun sendColorChangeMessage(color: String) {
        val json = JSONObject().apply {
            put("event", "colorChange")
            put("color", color)
            put("roomId", "roomId123")
        }
        webSocket.send(json.toString())
    }

    // 앱 종료 시 WebSocket 연결 종료
    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "App closed")
    }
}
