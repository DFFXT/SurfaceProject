package com.example.surfaceproject

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.media.ImageReader
import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaceproject.glsl.Loader
import com.example.surfaceproject.model.RectModelGLES
import com.example.surfaceproject.texture.BitmapTextureGLE
import com.example.surfaceproject.texture.TextureGLES
import com.example.surfaceproject.texture.textureSqure
import toBitmap

class MediaProjectionActivity : AppCompatActivity() {
    private val capture = ScreenCapture(this)
    lateinit var glEnvironment: OpenGLEnvironment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_projection)
        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        val textureView = findViewById<TextureView>(R.id.textureView)


        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {




                glEnvironment = OpenGLEnvironment()
                glEnvironment.createEnvironment(holder.surface) {
                    TextureGLES.init(2)
                    GLES20.glViewport(0, 0, 200, 200)
                    GLES20.glClearColor(1f, 0f, 0f, 1f)
                    val surfaceTexture = SurfaceTexture(TextureGLES.tid[1], false)
                    surfaceTexture.setDefaultBufferSize(200, 200)
                    val txtSurface = Surface(surfaceTexture)





                    val loader = Loader()
                    val txture = BitmapTextureGLE(1, null, loader = loader)
                    txture.load()
                    loader.load(R.raw.vertext, R.raw.fragment)
                    val v = floatArrayOf(
                        0f, 1f, 0f,
                        1f, 1f, 0f,
                        0f, 0f, 0f,
                        1f, 0f, 0f,
                    )
                    //Matrix.translateM(v, 0, 0.5f, 0.5f, 0f)
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
                    surfaceTexture.setOnFrameAvailableListener({
                        it.updateTexImage()
                        d()
                    }, Handler(Looper.myLooper()!!))
                    capture.startCapture(txtSurface, 200, 200)



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
}
