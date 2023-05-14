package com.example.surfaceproject.texture

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.surfaceproject.toBuffer
import javax.microedition.khronos.opengles.GL10

private val textureSqure = floatArrayOf(
    0f,
    1f,
    1f,
    1f,
    0f,
    0f,
    1f,
    0f,
).toBuffer()

class BitmapTexture(private val index: Int, private var bitmap: Bitmap?) : Texture() {
    val id: Int get() = tid[index]
    fun load(gl: GL10): Int {
        // gl.glActiveTexture(GL10.GL_TEXTURE0 + index)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id)
        // 设置采用方式, 每个纹理都必须设置
        gl.glTexParameterx(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR,
        )
        gl.glTexParameterx(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR,
        )

        if (bitmap != null) {
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap?.recycle()
            bitmap = null
        }
        return id
    }

    fun active(gl: GL10) {
        gl.glActiveTexture(id)
    }

    fun draw(gl: GL10) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureSqure)
    }
}
