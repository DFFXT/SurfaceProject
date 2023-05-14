package com.example.surfaceproject.model

import com.example.surfaceproject.texture.BitmapTexture
import com.example.surfaceproject.toBuffer
import javax.microedition.khronos.opengles.GL10

private val fx = floatArrayOf(
    0f, 0f, 1f,
    0f, 0f, 1f,
    0f, 0f, 1f,
).toBuffer()

class RectModel(vertex: FloatArray) {
    val vertexBuffer = vertex.toBuffer()
    var texture: BitmapTexture? = null
    fun draw(gl: GL10) {
        // 设置三角形组坐标
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        // 设置三角形法向, 贴图必须要
        gl.glNormalPointer(GL10.GL_FLOAT, 0, fx)
        // 设置纹理
        texture?.draw(gl)
        // 绘制图片
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4)
    }
}
