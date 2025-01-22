package com.example.gametset.room

import android.content.Context
import android.graphics.Path
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.gson.Gson

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val drawPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    private var drawPath = Path()
    private var pathSegments = mutableListOf<Path>()
    private var currentSegmentIndex = 0
    private var erasePath: Path = Path()
    private var erasePaint: Paint = Paint()
    private var paths: MutableList<Path> = mutableListOf()

    init {
        drawPaint.apply {
            color = Color.BLACK
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 10f
        }

        // 지우개(Paint) 설정
        erasePaint.isAntiAlias = true
        erasePaint.isDither = true
        erasePaint.color = Color.TRANSPARENT
        erasePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)  // 지우개 모드 설정
        erasePaint.style = Paint.Style.STROKE
        erasePaint.strokeJoin = Paint.Join.ROUND
        erasePaint.strokeCap = Paint.Cap.ROUND
        erasePaint.strokeWidth = 30f  // 지우개 크기 설정
    }

    // Path 추가
    fun addPathSegment(newPath: Path) {
        pathSegments.add(newPath)
    }

    fun startDrawingAnimation(interval: Long = 500L) {
        val handler = android.os.Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (currentSegmentIndex < pathSegments.size) {
                    drawNextSegment()
                    handler.postDelayed(this, interval)
                }
            }
        }
        handler.post(runnable)
    }

    fun drawNextSegment() {
        // 현재 인덱스가 세그먼트 리스트 범위 안에 있는지 확인
        if (currentSegmentIndex < pathSegments.size) {
            currentSegmentIndex++ // 다음 세그먼트로 이동
            invalidate() // 다시 그리기 요청
        }
    }


    // 그림 초기화
    fun clearDrawing() {
        pathSegments.clear()
        drawPath.reset()
        currentSegmentIndex = 0
        invalidate()
    }

    // 그림 재설정
    fun resetDrawing() {
        pathSegments.clear()
        currentSegmentIndex = 0
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0..currentSegmentIndex) {
            if (i < pathSegments.size) {
                canvas.drawPath(pathSegments[i], drawPaint)
            }
        }
        for (path in paths) {
            canvas.drawPath(path, drawPaint)
        }
        canvas.drawPath(drawPath, drawPaint)
    }

//    private fun sendPathDataToServer() {
//        val pathData = PathData(drawPath)
//        val json = Gson().toJson(pathData) // Convert path data to JSON
//        (context as MainActivity).sendDrawingData(json) // Send data to the WebSocket server
//    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(event.x, event.y)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(event.x, event.y)
                invalidate()
            }
        }

//        sendPathDataToServer()
        return true
    }

//    fun receiveDrawingData(data: String) {
//        val pathData = Gson().fromJson(data, PathData::class.java)
//        paths.add(pathData.path) // Add the received path to the list
//        invalidate() // Redraw the canvas
//    }
}

data class PathData(val path: Path)