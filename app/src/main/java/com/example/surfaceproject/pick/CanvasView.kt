package com.example.surfaceproject.pick

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import kotlin.math.max
import kotlin.math.min

class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private var rect = RectF()
    private var px1: Float = 0f
    private var px2: Float = 0f
    private var py1: Float = 0f
    private var py2: Float = 0f
    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private var offsetY = 0f
    private var offsetX = 0f

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val p = intArrayOf(0, 0)
        getLocationOnScreen(p)
        offsetY = p[1].toFloat()
        offsetX = p[0].toFloat()
        setWillNotDraw(false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                px1 = event.rawX
                py1 = event.rawY
                px2 = px1
                py2 = py1
            }

            MotionEvent.ACTION_MOVE -> {
                px2 = event.rawX
                py2 = event.rawY
            }
        }
        rect.set(min(px1, px2), min(py1, py2), max(px1, px2), max(py1, py2))
        postInvalidate()
        return true
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        canvas?.translate(-offsetX, -offsetY)
        canvas?.drawRect(rect, paint)
        canvas?.translate(offsetX, offsetY)
    }
}
