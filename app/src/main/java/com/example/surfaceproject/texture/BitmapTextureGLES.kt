package com.example.surfaceproject.texture

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import javax.microedition.khronos.opengles.GL10

class BitmapTextureGLES(private val index: Int, private var bitmap: Bitmap?) : TextureGLES() {
    val id: Int get() = tid[index]
    fun load(): Int {
        // gl.glActiveTexture(GL10.GL_TEXTURE0 + index)
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, id)
        // 设置采用方式, 每个纹理都必须设置
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR,
        )
        GLES20.glTexParameteri(
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

    fun draw() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        // gl.glBindTexture(GL10.GL_TEXTURE_2D, id)
        GLES20.glVertexAttribPointer(GLES20.GL_TEXTURE_2D, 0, 0, false, 0, textureSqure)
        // GLES20.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureSqure)
    }
}