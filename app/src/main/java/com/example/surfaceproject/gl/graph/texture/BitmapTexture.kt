package com.example.surfaceproject.gl.graph.texture

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.surfaceproject.gl.glsl.Loader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

// android 纹理坐标原点是左上角
// OpenGL 坐标原点是视图正中心

class BitmapTexture(loader: Loader, var bitmap: Bitmap? = null) : BaseTexture(createTexture2d(), loader) {
    override fun prepare() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
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
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap = null
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        // return id
    }
    fun updateBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        prepare()
    }
}
