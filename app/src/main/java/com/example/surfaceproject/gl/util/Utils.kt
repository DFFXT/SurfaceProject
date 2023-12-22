package com.example.surfaceproject.gl.util

import android.graphics.Point
import android.view.WindowManager
import com.example.surfaceproject.App
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 将浮点数数组转换为nativeOrder排序的FloatBuffer
 */
fun FloatArray.toNativeBuffer(): FloatBuffer {
    val b = ByteBuffer.allocateDirect(size * 4)
    b.order(ByteOrder.nativeOrder())
    val buffer = b.asFloatBuffer()
    buffer.put(this)
    buffer.position(0)
    return buffer
}

private val point = Point()
fun screenRealSize(): Point = run {
    val wm = App.ctx.getSystemService(WindowManager::class.java)
    wm.defaultDisplay.getRealSize(point)
    point
}
