package com.example.surfaceproject.texture

import android.opengl.GLES20

abstract class TextureGLES {
    companion object {
        lateinit var tid: IntArray
        fun init(count: Int) {
            tid = IntArray(count)
            GLES20.glGenTextures(count, tid, 0)
        }
    }
}
