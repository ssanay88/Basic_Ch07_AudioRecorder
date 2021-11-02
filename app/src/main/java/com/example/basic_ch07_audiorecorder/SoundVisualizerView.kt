package com.example.basic_ch07_audiorecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizerView(context: Context , attrs: AttributeSet? = null) : View(context,attrs) {

    var onRequestCurrentAmplitude: (() -> Int)? = null

    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {    // 계단화 방지 플래그
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND     // 라인의 양 끝을 어떻게 처리할 것인가?
    }

    private var drawingWidth:Int = 0
    private var drawingHeight:Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()

    private var isReplaying = false
    private var replayingPosition: Int = 0

    private val visualizeRepeatAction : Runnable = object : Runnable {
        override fun run() {

            // Amplitude 가져오기 , Draw
            if (!isReplaying) {
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes    // 제일 최근 진폭이 제일 앞에 있어야 한다
            } else {
                replayingPosition++
            }

            invalidate()    // 뷰 갱신을 위해 선언 -> 다시 Drawing

            handler?.postDelayed(this, ACTION_INTERVAL)    // 자기 자신을 20ms뒤에 다시 실행

        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingHeight = h
        drawingWidth = w
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return    // canvas가 null일 경우 진행 X

        val centerY = drawingHeight / 2f
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes.let { amplitudes ->
            if (isReplaying) {
                amplitudes.takeLast(replayingPosition)
            } else {
                amplitudes
            }
        }
            .forEach { amplitude ->
            val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F

            offsetX -= LINE_SPACE
            if (offsetX < 0) {
                return@forEach
            }

            canvas.drawLine(
                offsetX,
                centerY - lineLength / 2F,
                offsetX,
                centerY + lineLength / 2F,
                amplitudePaint
            )


        }

    }
    //
    fun startVisualizing(isReplaying:Boolean) {

        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)

    }

    fun stopVisualizing() {

        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)

    }

    fun clearVisualization() {
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()   // 진폭의 최대값 = short 값의 최대값으로 제한을 둔다 32767
        private const val ACTION_INTERVAL = 20L
    }

}