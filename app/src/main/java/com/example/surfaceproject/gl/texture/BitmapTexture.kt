package com.example.surfaceproject.gl.texture

import android.graphics.Bitmap
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.surfaceproject.gl.glsl.Loader
import com.example.surfaceproject.gl.util.screenRealSize
import com.example.surfaceproject.gl.util.toNativeBuffer
import java.nio.FloatBuffer

var top = 0f
var bottom = 0f
val size = screenRealSize()
val width = size.x
val height = size.y

// android 纹理坐标原点是左上角
// OpenGL 坐标原点是视图正中心
val textureSqure by lazy {
    padding(
        floatArrayOf(
            0f,
            0f,
            1f,
            0f,
            0f,
            1f,
            1f,
            1f,
        ),
        0f,
        top / height,
        1 - 200f / width,
        1 - bottom / height,
    ).toNativeBuffer()
}

fun padding(vertext: FloatArray, left: Float, top: Float, right: Float, bottom: Float): FloatArray {
    vertext[0] += left
    vertext[4] += left

    vertext[1] += top
    vertext[3] += top

    vertext[2] -= right
    vertext[6] -= right

    vertext[5] -= bottom
    vertext[7] -= bottom
    return vertext
}

class BitmapTextureGLE : Texture {
    var id: Int = 0
        private set
    private var bitmap: Bitmap? = null
    private var loader: Loader

    constructor(bitmap: Bitmap, loader: Loader) {
        id = createOesTextureId()
        this.bitmap = bitmap
        this.loader = loader
        load()
    }

    constructor(id: Int, loader: Loader) {
        this.id = id
        this.loader = loader
    }

    constructor(loader: Loader) {
        this.loader = loader
        this.id = createOesTextureId()
    }

    private var textureVertexPoint: FloatBuffer = TEXTURE_FULL_VERTEX

    init {
        //load()
    }

    private fun load(): Int {
        // gl.glActiveTexture(GL10.GL_TEXTURE0 + index)
        // GLES20.glBindTexture(GL10.GL_TEXTURE_2D, id)
        // GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, id)
        // 设置采用方式, 每个纹理都必须设置
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR,
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR,
        )

        if (bitmap != null) {
            GLUtils.texImage2D(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0, bitmap, 0)
            bitmap?.recycle()
            bitmap = null
        }
        return id
    }

    fun draw() {
        val location = GLES20.glGetAttribLocation(loader.program, "inputTextureCoordinate")
        GLES20.glEnableVertexAttribArray(location)
        GLES20.glVertexAttribPointer(location, 2, GLES20.GL_FLOAT, false, 0, textureVertexPoint)

        val textureLoc = GLES20.glGetUniformLocation(loader.program, "inputImageOESTexture")
        //  GLES20.glActiveTexture(id)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, id)
        GLES20.glUniform1i(textureLoc, 0)

        // GLES20.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureSqure)
    }
}
