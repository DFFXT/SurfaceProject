package com.example.surfaceproject

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.surfaceproject.glsl.Loader
import com.example.surfaceproject.media.SurfaceToMedia
import com.example.surfaceproject.model.RectModelGLES
import com.example.surfaceproject.texture.BitmapTextureGLE
import com.example.surfaceproject.texture.TextureGLES
import com.example.surfaceproject.texture.bottom
import com.example.surfaceproject.texture.top

class MediaProjectionActivity : AppCompatActivity() {
    private var recorder: SurfaceToMedia? = null
    private val capture = ScreenCapture(this)
    lateinit var glEnvironment: OpenGLEnvironment

    // SurfaceTexture 需要防止被回收
    lateinit var surfaceTexture: SurfaceTexture
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_projection)
        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        val textureView = findViewById<TextureView>(R.id.textureView)
        textureView.post {

        }

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val v  = findViewById<View>(R.id.target)
                val arr = intArrayOf(0,0)
                v.getLocationInWindow(arr)
                top = arr[1].toFloat()
                bottom = top + 200f
                glEnvironment = OpenGLEnvironment()
                // recorder = SurfaceToMedia(this@MediaProjectionActivity, 200, 200, holder.surface)

                glEnvironment.createEnvironment(holder.surface) {
                    TextureGLES.init(2)
                    GLES20.glViewport(0, 0, 200, 200)
                    GLES20.glClearColor(1f, 0f, 0f, 1f)
                    surfaceTexture = SurfaceTexture(TextureGLES.tid[1], false)
                    surfaceTexture.setDefaultBufferSize(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
                    val txtSurface = Surface(surfaceTexture)

                    val loader = Loader()
                    val txture = BitmapTextureGLE(1, null, loader = loader)
                    txture.load()
                    loader.load(R.raw.vertext, R.raw.fragment)
                    val v = floatArrayOf(
                        -1f, 1f, 0f,
                        1f, 1f, 0f,
                        -1f, -1f, 0f,
                        1f, -1f, 0f,
                    )
                    // Matrix.translateM(v, 0, 0f, 0f, 0f)
                    val rect = RectModelGLES(
                        v,
                        loader,
                    )
                    rect.texture = txture
                    fun d() {
                        it.draw {
                            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                            GLES20.glEnable(GLES20.GL_TEXTURE_2D)
                            rect.draw()
                        }
                    }
                    d()
                    surfaceTexture.setOnFrameAvailableListener({
                        val f = 0
                        it.updateTexImage()
                        d()
                    }, Handler(Looper.myLooper()!!))
                    capture.startCapture(txtSurface)
                    recorder?.start()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int,
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })
    }

    override fun onStop() {
        super.onStop()
        recorder?.stop()
        recorder?.release()
        recorder = null
    }
}
