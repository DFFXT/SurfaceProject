package com.example.surfaceproject.gl.glsl

import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.example.surfaceproject.R

/**
 * 矩形渲染
 */
class RectLoader : Loader() {
    init {
        load(R.raw.vertext, R.raw.luminance_frg)
    }

    /**
     * 启用顶点
     */
    fun enableAttributeLocation(): Int {
        val location = GLES20.glGetAttribLocation(program, "inputPosition")
        GLES20.glEnableVertexAttribArray(location)
        return location
    }

    /**
     * 启用纹理顶点
     */
    fun enableAttributeTextureLocation(): Int {
        val location = GLES20.glGetAttribLocation(program, "inputTextureCoordinate")
        GLES20.glEnableVertexAttribArray(location)
        return location
    }

    /**
     * 绑定纹理id
     */
    fun bindTextureId(id: Int): Int {
        val textureLoc = GLES20.glGetUniformLocation(program, "inputImageOESTexture")
        //  GLES20.glActiveTexture(id)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, id)
        return textureLoc
    }
}
