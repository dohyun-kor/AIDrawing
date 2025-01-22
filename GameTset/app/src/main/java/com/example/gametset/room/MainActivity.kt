package com.example.gametset.room

import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gametset.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

private const val TAG = "MainActivity_싸피"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var simulatedPaths: List<Path>
    private lateinit var webSocket: WebSocket
    private lateinit var wordAdapter: WordAdapter
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

        // Path 데이터를 생성
        simulatedPaths = createSimulatedPaths()

        // Start 버튼 클릭 시 그림 다시 그리기
        binding.startButton.setOnClickListener {
            val text = binding.answer.text.toString()
            if(text.isEmpty()){
                Toast.makeText(this,"입력창이 비었습니다!",Toast.LENGTH_SHORT).show()
            }
            else{
                sendMessage(text)
                wordAdapter.addWord(text, binding.wordRecyclerView)
            }
        }

        // Clear 버튼 클릭 시 그림판 지우기
        binding.clearButton.setOnClickListener {
            binding.drawingView.clearDrawing()
        }
    }

    // WebSocket 연결 설정
    private fun setupWebSocketConnection() {
        val serverUrl = "ws://192.168.100.203:9987/ws" // Spring WebSocket 서버 URL
//        val serverUrl = "ws://localhost:9987/ws" // Spring WebSocket 서버 URL

        val client = OkHttpClient()

        // WebSocketListener로 연결 처리
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                runOnUiThread {
                    // 서버로부터 받은 메시지를 처리
//                    Toast.makeText(this@MainActivity, "Received message: $text", Toast.LENGTH_SHORT)
//                        .show()
                    wordAdapter.addWord(text, binding.wordRecyclerView)
                    // WebSocket 메시지에 따라 그림 그리기 애니메이션 시작
                    when (text) {
                        "그림그리기" -> startDrawingAnimation()
                        "그림판청소" -> binding.drawingView.clearDrawing()
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                super.onFailure(webSocket, t, response)
                runOnUiThread {
                    Log.d(TAG, "onFailure: ${t.message}")
                    Log.d(TAG, "Response: ${response?.code} and ${response?.isSuccessful}")

                    // 오류 처리
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)

                // 서버에 메시지를 보내는 예시
                sendMessage("Hello from Android!")
            }
        })
    }

    // 메시지 전송
    private fun sendMessage(message: String) {
        webSocket.send(message)
    }

    // Path 데이터를 생성
    private fun createSimulatedPaths(): List<Path> {
        val paths = mutableListOf<Path>()

        // 별 모양 (Star) 세그먼트 추가
        paths.add(Path().apply { moveTo(150f, 50f); lineTo(200f, 200f) })
        paths.add(Path().apply { moveTo(200f, 200f); lineTo(50f, 100f) })
        paths.add(Path().apply { moveTo(50f, 100f); lineTo(250f, 100f) })
        paths.add(Path().apply { moveTo(250f, 100f); lineTo(100f, 200f) })
        paths.add(Path().apply { moveTo(100f, 200f); lineTo(150f, 50f) }) // 닫기

        // 나선형 (Spiral) 조각화
        val centerX = 700f
        val centerY = 700f
        val totalTurns = 4
        val maxRadius = 150f
        val spiralSegments = mutableListOf<Path>()

        for (i in 0 until 360 * totalTurns step 10) {
            val angle = Math.toRadians(i.toDouble())
            val radius = maxRadius * (i / (360.0 * totalTurns)).toFloat()
            val x = (centerX + radius * Math.cos(angle)).toFloat()
            val y = (centerY + radius * Math.sin(angle)).toFloat()
            val segment = Path()
            segment.moveTo(300f, 300f)
            if (i == 0) {
                segment.moveTo(300f, 300f)
            } else {
                segment.lineTo(x, y)
            }
            spiralSegments.add(segment)
        }

        // 자유 곡선 (Wave) 조각화
        val waveSegments = mutableListOf<Path>()
        val wavePath = Path()
        wavePath.moveTo(1000f,500f)
        for (i in 0..10) {
            val x = i * 50f
            val y = if (i % 2 == 0) 599f else 224f
            wavePath.lineTo(x, y)
            waveSegments.add(wavePath) // 각 점마다 Path 추가
        }
        paths.addAll(waveSegments)

        return paths
    }

    // Path 애니메이션 시작
    private fun startDrawingAnimation() {
        binding.drawingView.resetDrawing()
        for (path in simulatedPaths) {
            binding.drawingView.addPathSegment(path)
        }
        binding.drawingView.startDrawingAnimation(interval = 1000L)
    }

    // 앱 종료 시 WebSocket 연결 종료
    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "App closed")
    }
}