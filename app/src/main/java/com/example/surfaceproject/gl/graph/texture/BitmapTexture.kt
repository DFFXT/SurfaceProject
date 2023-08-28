package com.example.surfaceproject.gl.graph.texture

import android.graphics.Bitmap
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.surfaceproject.gl.glsl.RectLoader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

// android 纹理坐标原点是左上角
// OpenGL 坐标原点是视图正中心

class BitmapTexture : Texture {
    var id: Int = 0
        private set
    private var bitmap: Bitmap? = null
    private var loader: RectLoader
    private var textureVertexPoint: FloatBuffer = TEXTURE_FULL_VERTEX
    private var mutableVertex: FloatBuffer? = null
    private var paddingLeft = 0f
    private var paddingTop = 0f
    private var paddingRight = 0f
    private var paddingBottom = 0f

    constructor(bitmap: Bitmap, loader: RectLoader, vertex: FloatBuffer = TEXTURE_FULL_VERTEX) {
        id = createOesTextureId()
        this.bitmap = bitmap
        this.loader = loader
        this.textureVertexPoint = vertex
        load()
    }

    constructor(id: Int, loader: RectLoader, vertex: FloatBuffer = TEXTURE_FULL_VERTEX) {
        this.id = id
        this.loader = loader
        this.textureVertexPoint = vertex
    }

    constructor(loader: RectLoader, vertex: FloatBuffer = TEXTURE_FULL_VERTEX) {
        this.loader = loader
        this.id = createOesTextureId()
        this.textureVertexPoint = vertex
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

    // 重置顶点信息
    private fun mutable() {
        if (mutableVertex == null) {
            mutableVertex = ByteBuffer.allocateDirect(textureVertexPoint.capacity() * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        }
        textureVertexPoint.mark()
        textureVertexPoint.position(0)
        mutableVertex!!.position(0)
        for (i in 0 until 8) {
            mutableVertex!!.put(textureVertexPoint.get())
        }
        textureVertexPoint.reset()
        mutableVertex!!.position(0)
    }

    /**
     * 设置图片纹理的padding，
     * 设置了后只显示区域内的图片
     */
    fun padding(left: Float = paddingLeft, top: Float = paddingTop, right: Float = paddingRight, bottom: Float = paddingBottom) {
        mutable()
        paddingInternal(mutableVertex!!, left, top, right, bottom)
    }

    private fun paddingInternal(vertext: FloatBuffer, left: Float, top: Float, right: Float, bottom: Float) {
        this.paddingLeft = left
        this.paddingTop = top
        this.paddingRight = right
        this.paddingBottom = bottom
        vertext.put(0, vertext.get(0) + left)
        vertext.put(4, vertext.get(4) + left)

        vertext.put(1, vertext.get(1) + top)
        vertext.put(3, vertext.get(3) + top)

        vertext.put(2, vertext.get(2) - right)
        vertext.put(6, vertext.get(6) - right)

        vertext.put(5, vertext.get(5) - bottom)
        vertext.put(7, vertext.get(7) - bottom)
    }

    override fun draw() {
        val location = loader.enableAttributeTextureLocation()
        GLES20.glVertexAttribPointer(location, 2, GLES20.GL_FLOAT, false, 0, mutableVertex ?: textureVertexPoint)

        val textureLoc = loader.bindTextureId(id)
        GLES20.glUniform1i(textureLoc, 0)

        // GLES20.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureSqure)
    }
}
