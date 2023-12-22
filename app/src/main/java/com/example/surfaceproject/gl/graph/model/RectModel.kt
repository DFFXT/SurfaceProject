package com.example.surfaceproject.gl.graph.model

import android.opengl.GLES20
import com.example.surfaceproject.gl.glsl.Loader
import com.example.surfaceproject.gl.glsl.RectLoader
import com.example.surfaceproject.gl.graph.IDraw
import com.example.surfaceproject.gl.graph.texture.BitmapTexture
import com.example.surfaceproject.gl.util.toNativeBuffer

val color = floatArrayOf(1f, 1f, 1f, 1f)

class RectModel(vertex: FloatArray, private val loader: RectLoader) : IDraw {
    private val vertexBuffer = vertex.toNativeBuffer()
    var texture: BitmapTexture? = null
    override fun draw() {
        loader.use()
        // 设置三角形组坐标
        val location = loader.enableAttributeLocation()

        GLES20.glVertexAttribPointer(location, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer)
        // 设置三角形法向, 贴图必须要

        // gl.glNormalPointer(GL10.GL_FLOAT, 0, fx)

        // val colorLocation = GLES20.glGetUniformLocation(loader.program, "v_color")
        // /GLES20.glUniform4fv(colorLocation, 1, color, 0)

        // 设置纹理
        texture?.draw()

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(location)
        GLES20.glUseProgram(0)
    }
}
