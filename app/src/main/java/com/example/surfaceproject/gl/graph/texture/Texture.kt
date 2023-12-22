package com.example.surfaceproject.gl.graph.texture

import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.example.surfaceproject.gl.graph.IDraw
import com.example.surfaceproject.gl.util.toNativeBuffer

abstract class Texture : IDraw {
    companion object {

        // 全部填充的矩形
        val TEXTURE_FULL_VERTEX = floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f).toNativeBuffer().asReadOnlyBuffer()

        fun createOesTextureId(): Int {
            val tid = IntArray(1)
            GLES20.glGenTextures(1, tid, 0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tid[0])
            // 环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
            // 环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
            // GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            // GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            // 过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
            // 过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

            // 解绑扩展纹理

            // 解绑扩展纹理
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
            return tid[0]
        }

        fun delete(id: Int) {
            GLES20.glDeleteTextures(1, intArrayOf(id), 0)
        }
    }
}
