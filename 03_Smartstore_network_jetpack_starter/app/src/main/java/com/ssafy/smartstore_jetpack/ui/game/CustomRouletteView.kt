package com.ssafy.smartstore_jetpack.ui.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import com.ssafy.smartstore_jetpack.ui.game.RouletteFragment.Companion.probabilities

private const val TAG = "CustomRouletteView_싸피"

class CustomRouletteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 40f // 텍스트 크기
        color = Color.BLACK // 텍스트 색상
    }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW // 선택된 섹션 강조 색상
        style = Paint.Style.STROKE
        strokeWidth = 10f // 강조할 선의 두께
    }

    private var rouletteDataList: List<String> = emptyList()
    private var shapeColors: List<Int> = emptyList()
    private var centerX = 0f
    private var centerY = 0f
    private var currentRotationDegrees = 0f // 현재 회전 각도
    private var selectedIndex: Int = -1 // 선택된 구간의 인덱스

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (rouletteDataList.size < 2) {
            return
        }

        val radius = (width.coerceAtMost(height)) / 2f - 20
        val rectF = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        // 룰렛 그리기
        drawRoulette(canvas, rectF)
    }

    private fun drawRoulette(canvas: Canvas, rectF: RectF) {
        val totalProbability = probabilities.sum()  // 확률의 총합 (1이어야 함)
        var startAngle = -90f  // 0도를 위쪽으로 시작

        canvas.save() // 회전 전에 캔버스 상태 저장
        canvas.rotate(currentRotationDegrees, centerX, centerY) // 룰렛만 회전

        rouletteDataList.forEachIndexed { index, text ->
            val sweepAngle = 360f * probabilities[index] / totalProbability  // 확률에 비례하는 섹터 각도 계산
            fillPaint.color = shapeColors[index % shapeColors.size] // 색상 설정

            // 섹터 그리기
            canvas.drawArc(rectF, startAngle, sweepAngle, true, fillPaint)

            // 텍스트 위치 계산
            val medianAngle = Math.toRadians(startAngle + sweepAngle / 2.0)
            val textX = (centerX + rectF.width() / 2.5 * Math.cos(medianAngle)).toFloat()
            val textY = (centerY + rectF.height() / 2.5 * Math.sin(medianAngle)).toFloat()

            canvas.drawText(text, textX, textY, textPaint)

            startAngle += sweepAngle // 이전 각도에 현재 구역의 각도를 더해줌
        }

        canvas.restore() // 캔버스 상태 복원
    }


    fun rotateRouletteByProbability(degree: Float) {
        val rotateAnimation = RotateAnimation(
            0F, degree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            this.duration = 2000 // 1초 동안 애니메이션 실행
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    // 애니메이션 종료 후 결과 처리
                    currentRotationDegrees = degree % 360
                    selectedIndex = getSelectedIndex(currentRotationDegrees)
                    val selectedItem = rouletteDataList[selectedIndex]
                    onRouletteRotateEnd?.invoke(selectedItem) // 결과 콜백
                    // 애니메이션 종료 후 초기화
                    resetRoulette()
                }
            })
        }
        startAnimation(rotateAnimation)

    }
    
    private fun resetRoulette() {
        // 룰렛을 초기 상태로 되돌림 (회전각 0)
        currentRotationDegrees = 0f
        invalidate() // 뷰 다시 그리기
    }


    private fun getSelectedIndex(currentRotationDegrees: Float): Int {
        val cumulativeProbabilities = probabilities
        var cumulativeAngle = 0f
        Log.d(TAG, "getSelectedIndex: $currentRotationDegrees")

        // 뒤에서부터 각 확률에 해당하는 각도를 누적하여 현재 각도에 해당하는 섹션을 찾음
        for (index in cumulativeProbabilities.size - 1 downTo 0) {
            val sectionAngle = cumulativeProbabilities[index] * 360 // 확률에 비례한 각도
            cumulativeAngle += sectionAngle

            // 누적된 각도가 currentRotationDegrees보다 크면 해당 구역이 선택됨
            if (currentRotationDegrees < cumulativeAngle) {
                return index
            }
        }

        return 0 // 마지막 구역을 선택
    }


    fun setRouletteData(dataList: List<String>, colors: List<Int>, callback: (String) -> Unit) {
        if (dataList.size < 2) throw IllegalArgumentException("Roulette requires at least 2 sections")

        rouletteDataList = dataList
        shapeColors = colors
        onRouletteRotateEnd = callback
        invalidate() // 뷰 다시 그리기
    }

    private var onRouletteRotateEnd: ((String) -> Unit)? = null // 회전 완료 콜백
}
