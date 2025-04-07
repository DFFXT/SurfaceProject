package com.example.surfaceproject.gl.glsl

import android.opengl.GLES20
import com.example.surfaceproject.R

/**
 * 矩形渲染
 */
class Rect2dLoader : Loader() {
    init {
        load(R.raw.vertext, R.raw.fragment2d)
    }

    /**
     * 启用顶点
     */
    override fun enableAttributeLocation(): Int {
        val location = GLES20.glGetAttribLocation(program, "inputPosition")
        GLES20.glEnableVertexAttribArray(location)
        return location
    }

    /**
     * 启用纹理顶点
     */
    override fun enableAttributeTextureLocation(): Int {
        val location = GLES20.glGetAttribLocation(program, "inputTextureCoordinate")
        GLES20.glEnableVertexAttribArray(location)
        return location
    }

    /**
     * 绑定纹理id
     */
    override fun bindTextureId(id: Int): Int {
        val textureLoc = GLES20.glGetUniformLocation(program, "uTexture")
        //  GLES20.glActiveTexture(id)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        return textureLoc
    }
}
