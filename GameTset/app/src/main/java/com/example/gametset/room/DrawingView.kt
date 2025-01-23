package com.example.gametset.room

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import okhttp3.WebSocket
import org.json.JSONObject

private const val TAG = "DrawingView_싸피"

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    val drawPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    private var path = Path()
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var webSocket: WebSocket? = null // WebSocket을 `MainActivity`에서 전달받도록 변경

    fun setWebSocket(socket: WebSocket) {
        this.webSocket = socket
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paths.forEach { (path, paint) ->
            canvas.drawPath(path, paint)
        }
        canvas.drawPath(path, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path = Path()
                path.moveTo(event.x, event.y)

                // 새로운 Paint 객체 생성
                val newPaint = Paint().apply {
                    color = drawPaint.color
                    style = Paint.Style.STROKE
                    strokeWidth = 10f
                    isAntiAlias = true
                }
                paths.add(Pair(path, newPaint))
                sendDrawData(event.x, event.y, MOVE_MODE)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                sendDrawData(event.x, event.y, DRAW_MODE)
            }
        }
        invalidate()
        return true
    }


    private fun sendDrawData(x: Float, y: Float, mode: Int) {
        val json = JSONObject()
        json.put("event", "draw")
        json.put("x", x)
        json.put("y", y)
        json.put("roomId", "roomId123")

        // Color Int 값을 HEX 문자열로 변환
        val colorHex = String.format("#%06X", 0xFFFFFF and drawPaint.color)
        json.put("color", colorHex)

        json.put("mode", mode)

        webSocket?.send(json.toString()) // WebSocket을 통해 데이터 전송
    }


    fun clearDrawing() {
        paths.clear()
        path.reset()
        invalidate()
    }

    fun drawFromServer(x: Float, y: Float, color: String, mode: Int) {
        val serverPaint = Paint().apply {
            this.color = Color.parseColor(color)
            this.style = Paint.Style.STROKE
            this.strokeWidth = 10f
            isAntiAlias = true
        }

        if (mode == MOVE_MODE || paths.isEmpty() || path.isEmpty) {
            // 새로운 경로 생성 (색상이 바뀌었을 가능성도 있음)
            path = Path()
            path.moveTo(x, y)
            paths.add(Pair(path, serverPaint)) // 새 경로와 새로운 색상을 저장
        } else {
            // 가장 최근의 path에 대해 색상 업데이트
            if (paths.isNotEmpty()) {
                paths[paths.size - 1] = Pair(paths.last().first, serverPaint)
            }
            // 기존 경로에 이어서 선을 그림 (이때도 새로운 색상 적용)
            path.lineTo(x, y)

        }

        invalidate()
    }



    fun sendClearDrawing() {
        val json = JSONObject()
        json.put("event", "clearDrawing")
        json.put("roomId", "roomId123")
        Log.d(TAG, "sendClearDrawing: $json")
        webSocket?.send(json.toString()) // 전달받은 WebSocket을 통해 메시지 전송
    }

    companion object{
        val DRAW_MODE = 1;
        val MOVE_MODE = 2;
    }
}
