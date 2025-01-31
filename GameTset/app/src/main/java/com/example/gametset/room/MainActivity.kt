package com.example.gametset.room

import android.graphics.Path
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wordAdapter = WordAdapter(wordList)
        binding.wordRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.wordRecyclerView.adapter = wordAdapter

        // WebSocket 연결 초기화
        setupWebSocketConnection()

        // DrawingView에 WebSocket 전달
        binding.drawingView.setWebSocket(webSocket)

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
            imageView.setOnClickListener{
                drawingView.drawPaint.color = ContextCompat.getColor(this@MainActivity, R.color.red)
                sendColorChangeMessage("#FF0000") // 빨간색으로 변경
            }
            imageView2.setOnClickListener {
                drawingView.drawPaint.color = ContextCompat.getColor(this@MainActivity, R.color.orange)
                sendColorChangeMessage("#FF7F50")
            }
            imageView3.setOnClickListener {
                drawingView.drawPaint.color = ContextCompat.getColor(this@MainActivity, R.color.yellow)
                sendColorChangeMessage("#FFFF00")
            }
            imageView4.setOnClickListener {
                drawingView.drawPaint.color = ContextCompat.getColor(this@MainActivity, R.color.green)
                sendColorChangeMessage("#ADFF2F")
            }
            imageView5.setOnClickListener {
                drawingView.drawPaint.color = ContextCompat.getColor(this@MainActivity, R.color.blue)
                sendColorChangeMessage("#0000FF")
            }
            imageView6.setOnClickListener {
                drawingView.drawPaint.color = ContextCompat.getColor(this@MainActivity, R.color.navy_blue)
                sendColorChangeMessage("#000080")
            }
            imageView7.setOnClickListener {
                drawingView.drawPaint.color = ContextCompat.getColor(this@MainActivity, R.color.purple)
                sendColorChangeMessage("#800080")
            }
            imageView8.setOnClickListener {
                drawingView.drawPaint.color = ContextCompat.getColor(this@MainActivity, R.color.black)
                sendColorChangeMessage("#FF000000")
            }
        }
    }

    // WebSocket 연결 설정
    private fun setupWebSocketConnection() {
//        val serverUrl = "ws://192.168.100.203:9987/api/ws"
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

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                super.onFailure(webSocket, t, response)
                runOnUiThread {
                    Log.d(TAG, "onFailure: ${t.message}")
                    Log.d(TAG, "Response: ${response?.code} and ${response?.isSuccessful}")

                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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
