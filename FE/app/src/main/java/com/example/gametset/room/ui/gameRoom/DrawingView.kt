package com.example.gametset.room.ui.gameRoom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import okhttp3.WebSocket
import org.json.JSONObject

private const val TAG = "DrawingView_싸피"

// 커스텀 뷰인 DrawingView 클래스 정의
class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    // 그림을 그리기 위한 Paint 객체 생성
    val drawPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    // 지우개 모드 활성화 여부
    var eraserEnabled = false

    // 지우개 모드에서 사용할 Paint 객체
    val erasePaint = Paint().apply {
        color = Color.WHITE  // 여기서 배경색과 같은 색을 설정
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    var roomId: Int = -1

    // 현재 그리는 경로를 저장할 Path 객체
    private var path = Path()

    // 저장된 경로와 해당 경로의 Paint 객체를 저장할 리스트
    private val paths = mutableListOf<Pair<Path, Paint>>()

    // WebSocket 객체 (GameWebSocketManager에서 받아올 예정)
    private var webSocket: WebSocket? = null

    // WebSocket 객체를 설정하는 메소드
    fun setWebSocket(socket: WebSocket?) {
        this.webSocket = socket
    }

    // 뷰에 그림을 그리는 메소드
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 저장된 모든 경로를 그리기
        paths.forEach { (path, paint) ->
            canvas.drawPath(path, paint)
        }
        // 현재 경로를 그리기
        canvas.drawPath(path, drawPaint)
    }

    // 터치 이벤트를 처리하는 메소드
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false  // 비활성화 상태면 터치 이벤트 무시

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 새로운 경로 생성 및 터치 위치로 이동
                path = Path()
                path.moveTo(event.x, event.y)

                // 새로운 Paint 객체 생성 및 경로와 Paint 객체를 리스트에 추가
                val newPaint = if (eraserEnabled) Paint(erasePaint) else Paint(drawPaint)
                paths.add(Pair(path, newPaint))
                sendDrawData(event.x, event.y, MOVE_MODE)
            }

            MotionEvent.ACTION_MOVE -> {
                // 경로에 선 추가
                path.lineTo(event.x, event.y)
                sendDrawData(event.x, event.y, DRAW_MODE)
            }
        }
        // 뷰를 다시 그리기
        invalidate()
        return true
    }

    // WebSocket을 통해 그리기 데이터를 전송하는 메소드
    private fun sendDrawData(x: Float, y: Float, mode: Int) {
        val scaledX = x / width
        val scaledY = y / height

        val json = JSONObject().apply {
            put("event", "draw")
            put("x", scaledX)
            put("y", scaledY)
            put("roomId", roomId)
            put(
                "color",
                String.format(
                    "#%06X",
                    0xFFFFFF and (if (eraserEnabled) erasePaint.color else drawPaint.color)
                )
            )
            put("mode", mode)
        }

        webSocket?.send(json.toString())
    }

    // 그림을 지우는 메소드
    fun clearDrawing() {
        paths.clear()
        path.reset()
        invalidate()
    }

    // 서버에서 전달받은 색상으로 색상을 업데이트하는 메소드
    fun updateColorFromServer(color: String) {
        drawPaint.color = Color.parseColor(color)
    }

    // 서버에서 받은 데이터로 그림을 그리는 메소드
    fun drawFromServer(x: Float, y: Float, color: String, mode: Int, stroke: Float) {
        val restoredX = x * width
        val restoredY = y * height

        val serverPaint = Paint().apply {
            this.color = Color.parseColor(color)
            this.style = Paint.Style.STROKE
            this.strokeWidth = stroke
            isAntiAlias = true
        }

        if (mode == MOVE_MODE || paths.isEmpty() || path.isEmpty) {
            path = Path()
            path.moveTo(restoredX, restoredY)
            paths.add(Pair(path, serverPaint))
        } else {
            if (paths.isNotEmpty()) {
                paths[paths.size - 1] = Pair(paths.last().first, serverPaint)
            }
            path.lineTo(restoredX, restoredY)
        }

        invalidate()
    }

    // WebSocket을 통해 그림 지우기 명령을 전송하는 메소드
    fun sendClearDrawing() {
        val json = JSONObject().apply {
            put("event", "cleardrawing")
            put("roomId", roomId)
        }
        Log.d(TAG, "sendCleardrawing: $json")
        webSocket?.send(json.toString())
    }

    fun passingRoomId(instanceRoomId: Int) {
        roomId = instanceRoomId
    }


    companion object {
        const val DRAW_MODE = 1
        const val MOVE_MODE = 2
    }
}