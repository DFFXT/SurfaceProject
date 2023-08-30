package com.example.surfaceproject.pick.gl

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Handler
import android.os.Looper
import android.view.Surface
import com.example.surfaceproject.gl.OpenGLEnvironment
import com.example.surfaceproject.gl.glsl.RectLoader
import com.example.surfaceproject.gl.graph.model.RectModel
import com.example.surfaceproject.gl.graph.texture.BitmapTexture
import com.example.surfaceproject.gl.util.screenRealSize

class ScreenRecordGLRender {
    private lateinit var surfaceTexture: SurfaceTexture
    lateinit var surface: Surface
    var width: Int = 0
        private set
    var height: Int = 0
        private set
    private lateinit var glEnvironment: OpenGLEnvironment

    private lateinit var bitmapTexture: BitmapTexture

    /**
     * 创建gl环境
     */
    fun create(surface: Surface, onStart: Runnable) {
        if (this::glEnvironment.isInitialized) return
        glEnvironment = OpenGLEnvironment()
        val screenSize = screenRealSize()
        glEnvironment.createEnvironment(surface, null) {
            GLES20.glViewport(0, 0, 1, 1)
            GLES20.glClearColor(1f, 0f, 0f, 1f)
            val loader = RectLoader()
            bitmapTexture = BitmapTexture(loader)
            surfaceTexture = SurfaceTexture(bitmapTexture.id, false)
            surfaceTexture.setDefaultBufferSize(screenSize.x, screenSize.y)
            this.surface = Surface(surfaceTexture)

            val v = floatArrayOf(
                -1f, 1f, 0f,
                1f, 1f, 0f,
                -1f, -1f, 0f,
                1f, -1f, 0f,
            )
            val rect = RectModel(
                v,
                loader,
            )
            rect.texture = bitmapTexture
            val arr = FloatArray(16)
            fun d() {
                it.draw {
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                    GLES20.glEnable(GLES20.GL_TEXTURE_2D)
                    GLES20.glEnable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES)
                    rect.draw()
                }
            }
            d()

            surfaceTexture.setOnFrameAvailableListener({
                val f = 0
                it.updateTexImage()
                it.getTransformMatrix(arr)
                d()
            }, Handler(Looper.myLooper()!!))
            onStart.run()
        }
    }

    fun setPadding(left: Float, top: Float, right: Float, bottom: Float) {
        bitmapTexture.padding(left, top, right, bottom)
    }

    fun bindSurface(surface: Surface) {
        if (::glEnvironment.isInitialized) {
            glEnvironment.bindSurface(surface)
        }
    }

    fun removeSurface(surface: Surface) {
        glEnvironment.removeSurface(surface)
    }

    fun runOnGLThread(runnable: Runnable) {
        glEnvironment.post(runnable)
    }

    fun onSurfaceSizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
        runOnGLThread {
            GLES20.glViewport(0, 0, width, height)
        }
    }

    fun release() {
        surface.release()
        surfaceTexture.release()
        glEnvironment.release()
    }
}
