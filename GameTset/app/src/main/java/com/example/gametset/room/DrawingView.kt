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
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                sendDrawData(event.x, event.y)
            }
        }
        invalidate()
        return true
    }


    private fun sendDrawData(x: Float, y: Float) {
        val json = JSONObject()
        json.put("event", "draw")
        json.put("x", x)
        json.put("y", y)
        json.put("roomId", "roomId123")
        json.put("color", "#000000") // 기본 검정색

        webSocket?.send(json.toString()) // `webSocket?.send()`를 사용하여 전달받은 WebSocket을 통해 전송
    }

    fun clearDrawing() {
        paths.clear()
        path.reset()
        invalidate()
        sendClearDrawing()
    }

    fun drawFromServer(x: Float, y: Float, color: String) {
        val serverPaint = Paint().apply {
            this.color = Color.parseColor(color)
            this.style = Paint.Style.STROKE
            this.strokeWidth = 10f
        }

        path = Path()
        path.moveTo(x, y)
        paths.add(Pair(path, serverPaint))
        invalidate()
    }

    private fun sendClearDrawing() {
        val json = JSONObject()
        json.put("event", "clearDrawing")
        json.put("roomId", "roomId123")
        Log.d(TAG, "sendClearDrawing: $json")
        webSocket?.send(json.toString()) // 전달받은 WebSocket을 통해 메시지 전송
    }
}
