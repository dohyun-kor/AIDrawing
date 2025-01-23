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
            binding.drawingView.clearDrawing()
        }

        binding.apply {
            imageView.setOnClickListener{
                drawingView.drawPaint.color= ContextCompat.getColor(this@MainActivity, R.color.red)
            }
            imageView2.setOnClickListener {
                drawingView.drawPaint.color= ContextCompat.getColor(this@MainActivity, R.color.orange)
            }
            imageView3.setOnClickListener {
                drawingView.drawPaint.color= ContextCompat.getColor(this@MainActivity, R.color.yellow)
            }
            imageView4.setOnClickListener {
                drawingView.drawPaint.color= ContextCompat.getColor(this@MainActivity, R.color.green)
            }
            imageView5.setOnClickListener {
                drawingView.drawPaint.color= ContextCompat.getColor(this@MainActivity, R.color.blue)
            }
            imageView6.setOnClickListener {
                drawingView.drawPaint.color= ContextCompat.getColor(this@MainActivity, R.color.navy_blue)
            }
            imageView7.setOnClickListener {
                drawingView.drawPaint.color= ContextCompat.getColor(this@MainActivity, R.color.purple)
            }
            imageView8.setOnClickListener {
                drawingView.drawPaint.color= ContextCompat.getColor(this@MainActivity, R.color.black)
            }

        }
    }

    // WebSocket 연결 설정
    private fun setupWebSocketConnection() {
        val serverUrl = "ws://192.168.100.203:9987/ws"

        val client = OkHttpClient()
        val request = Request.Builder().url(serverUrl).build()

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
                                if (x >= 0 && y >= 0) {
                                    binding.drawingView.drawFromServer(x, y, color)
                                }
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

    // 앱 종료 시 WebSocket 연결 종료
    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "App closed")
    }
}
