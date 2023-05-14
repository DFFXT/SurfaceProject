package com.example.surfaceproject.texture

import javax.microedition.khronos.opengles.GL10

abstract class Texture {
    companion object {
        lateinit var tid: IntArray
        fun init(gl: GL10, count: Int) {
            tid = IntArray(count)
            gl.glGenTextures(count, tid, 0)
        }
    }
}