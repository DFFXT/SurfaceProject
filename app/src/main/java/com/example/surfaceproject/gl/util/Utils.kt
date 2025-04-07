package com.example.surfaceproject.gl.util

import android.graphics.Bitmap
import android.graphics.Point
import android.opengl.GLES20
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


fun captureScreenshot(width: Int, height: Int): Bitmap {
    // 分配内存存储像素数据
    val buffer = ByteBuffer.allocateDirect(width * height * 4)
    buffer.order(ByteOrder.nativeOrder())

    // 读取帧缓冲区内容
    GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer)

    // 翻转像素数据
    val pixelData = ByteArray(width * height * 4)
    buffer.get(pixelData)
    flipPixels(pixelData, width, height)

    // 创建 Bitmap 并保存为 PNG 文件
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(pixelData))
    return bitmap
}

private fun flipPixels(pixels: ByteArray, width: Int, height: Int) {
    val stride = width * 4
    val row = ByteArray(stride)
    for (y in 0 until height / 2) {
        val offset1 = y * stride
        val offset2 = (height - y - 1) * stride
        System.arraycopy(pixels, offset1, row, 0, stride)
        System.arraycopy(pixels, offset2, pixels, offset1, stride)
        System.arraycopy(row, 0, pixels, offset2, stride)
    }
}
