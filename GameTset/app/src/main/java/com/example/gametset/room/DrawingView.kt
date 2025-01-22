package com.example.gametset.room

import android.content.Context
import android.graphics.Path
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    private var drawPath = Path()
    private var pathSegments = mutableListOf<Path>()
    private var currentSegmentIndex = 0

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
                canvas.drawPath(pathSegments[i], paint)
            }
        }
    }
}
