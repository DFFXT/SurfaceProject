package com.example.surfaceproject.gl.graph.texture

import android.opengl.GLES20
import com.example.surfaceproject.gl.glsl.Loader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

// android 纹理坐标原点是左上角
// OpenGL 坐标原点是视图正中心

open class BaseTexture(val id: Int, loader: Loader) : Texture(loader) {
    private var textureVertexPoint: FloatBuffer = TEXTURE_FULL_VERTEX
    private var mutableVertex: FloatBuffer? = null
    private var paddingLeft = 0f
    private var paddingTop = 0f
    private var paddingRight = 0f
    private var paddingBottom = 0f


    open fun prepare() {

    }

    private fun load(): Int {
        // gl.glActiveTexture(GL10.GL_TEXTURE0 + index)
        // GLES20.glBindTexture(GL10.GL_TEXTURE_2D, id)
        // GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        // bitmap不能是GLES11Ext.GL_TEXTURE_EXTERNAL_OES纹理
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

        /*if (bitmap != null) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap?.recycle()
            bitmap = null
        }*/
        return id
    }

    // 重置顶点信息
    private fun mutable() {
        // 每次都创建新的buffer
        //if (mutableVertex == null) {
            mutableVertex = ByteBuffer.allocateDirect(TEXTURE_FULL_VERTEX.capacity() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
       // }
        mutableVertex!!.position(0)
        for (i in 0 until 8) {
            mutableVertex!!.put(TEXTURE_FULL_VERTEX.get(i))
        }
        mutableVertex!!.position(0)
    }

    /**
     * 设置图片纹理的padding，
     * 设置了后只显示区域内的图片
     * 由于glVertexAttribPointer最后参数是用的buffer的ptr，所以更改buffer内部的数据不会影响最终的显示效果，需要使用不同的buffer对象才能更新
     */
    fun padding(
        left: Float = paddingLeft,
        top: Float = paddingTop,
        right: Float = paddingRight,
        bottom: Float = paddingBottom
    ) {
        if (paddingLeft == left && paddingTop == top && paddingRight == right && paddingBottom == bottom) {
            textureVertexPoint = mutableVertex ?: textureVertexPoint
            return
        }
        mutable()
        paddingInternal(mutableVertex!!, left, top, right, bottom)
        textureVertexPoint = mutableVertex ?: textureVertexPoint
    }

    /**
     * 重置padding
     */
    fun resetPadding() {
        textureVertexPoint = TEXTURE_FULL_VERTEX
    }

    private fun paddingInternal(
        vertext: FloatBuffer,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
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
       // LogUtil.e("GL_ERROR", "GL_ERROR--7.0> ${GLES20.glGetError()}")
        val location = loader.enableAttributeTextureLocation()
        //LogUtil.e("GL_ERROR", "GL_ERROR--7.1> ${GLES20.glGetError()}")
        GLES20.glVertexAttribPointer(
            location,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            textureVertexPoint
        )
        //LogUtil.e("GL_ERROR", "GL_ERROR--7.2> ${GLES20.glGetError()}")

        val textureLoc = loader.bindTextureId(id)
        GLES20.glUniform1i(textureLoc, 0)

        // GLES20.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureSqure)
    }
}
