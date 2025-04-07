package com.example.surfaceproject.gl.glsl

import android.opengl.GLES20
import com.example.surfaceproject.App

/**
 * 加载顶点着色器和片着色器
 */
open class Loader {
    var program: Int = -1
        private set

    private fun compile(type: Int, raw: Int): Int {
        val vertexShader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(vertexShader, read(raw))
        GLES20.glCompileShader(vertexShader)
        return vertexShader
    }

    protected fun load(vertexRaw: Int, fragmentRaw: Int): Int {
        val vertexShader = compile(GLES20.GL_VERTEX_SHADER, vertexRaw)
        val fragmentShader = compile(GLES20.GL_FRAGMENT_SHADER, fragmentRaw)
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        GLES20.glUseProgram(program)
        this.program = program
        return program
    }
    open fun enableAttributeLocation(): Int {
        return 0
    }

    open fun enableAttributeTextureLocation(): Int {
        return 0
    }


    open fun bindTextureId(id: Int): Int {
        return 0
    }


    private fun read(raw: Int): String {
        App.ctx.resources.openRawResource(raw).use {
            return String(it.readBytes())
        }
    }

    fun use() {
        GLES20.glUseProgram(program)
    }
}
