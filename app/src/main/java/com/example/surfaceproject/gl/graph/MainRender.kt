package com.example.surfaceproject.gl.graph

import android.graphics.SurfaceTexture
import android.opengl.EGLContext
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Handler
import android.os.Looper
import android.view.Surface
import com.example.surfaceproject.OpenGLEnvironment
import com.example.surfaceproject.gl.glsl.RectLoader
import com.example.surfaceproject.gl.graph.model.RectModel
import com.example.surfaceproject.gl.graph.texture.BitmapTexture
import com.example.surfaceproject.gl.util.screenRealSize

class MainRender {
    lateinit var surfaceTexture: SurfaceTexture
    lateinit var surface: Surface
    private lateinit var glEnvironment: OpenGLEnvironment
    companion object {
        lateinit var bitmapTexture: BitmapTexture
        var shareEGLContext: EGLContext? = null
    }

    fun start(surface: Surface, onStart: Runnable) {
        /*val v = findViewById<View>(R.id.target)
        val arr = intArrayOf(0, 0)
        v.getLocationInWindow(arr)
        val top = arr[1].toFloat()
        val bottom = top + 200f
        val size = screenRealSize()
        val width = size.x
        val height = size.y*/
        if (this::glEnvironment.isInitialized) {
            glEnvironment.bindSurface(surface)
            return
        }
        glEnvironment = OpenGLEnvironment()
        // recorder = SurfaceToMedia(this@MediaProjectionActivity)

        val screenSize = screenRealSize()

        glEnvironment.createEnvironment(surface, shareEGLContext) {
            shareEGLContext = it.eglContext
            // Texture.init(1)
            GLES20.glViewport(0, 0, 200, 200)
            GLES20.glClearColor(1f, 0f, 0f, 1f)
            val loader = RectLoader()
            bitmapTexture = BitmapTexture(loader)
            // txture.padding(0f, top / height, 1 - 200f / width, 1 - bottom / height)
            surfaceTexture = SurfaceTexture(bitmapTexture.id, false)
            surfaceTexture.setDefaultBufferSize(screenSize.x, screenSize.y)
            this.surface = Surface(surfaceTexture)

            // val shader = LuminanceFilter(this@MediaProjectionActivity)

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
                    // shader.onDraw(TextureGLES.tid[0], arr, 0 ,0, 200,200)
                }
            }
            d()

            surfaceTexture.setOnFrameAvailableListener({
                val f = 0
                it.updateTexImage()
                it.getTransformMatrix(arr)
                d()
            }, Handler(Looper.myLooper()!!))
            // capture.startCapture(txtSurface, screenSize.x, screenSize.y)
            // recorder?.start()
            onStart.run()
        }
    }

    fun setPadding(left: Float, top: Float, right: Float, bottom: Float) {
        bitmapTexture.padding(left, top, right, bottom)
    }
    fun bindSurface(surface: Surface) {
        glEnvironment.bindSurface(surface)
    }
}
