package com.example.surfaceproject

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaceproject.gl.glsl.Loader
import com.example.surfaceproject.gl.model.RectModelGLES
import com.example.surfaceproject.gl.texture.BitmapTextureGLE
import com.example.surfaceproject.gl.texture.Texture
import com.example.surfaceproject.gl.texture.bottom
import com.example.surfaceproject.gl.texture.top
import com.example.surfaceproject.gl.util.screenRealSize
import com.example.surfaceproject.media.SurfaceToMedia

class MediaProjectionActivity : AppCompatActivity() {
    private var recorder: SurfaceToMedia? = null
    private val capture = ScreenCapture(this)
    lateinit var glEnvironment: OpenGLEnvironment

    // SurfaceTexture 需要防止被回收
    lateinit var surfaceTexture: SurfaceTexture
    private val loopRun = object : Runnable {
        override fun run() {
            findViewById<View>(R.id.target).post(this)
            findViewById<TextView>(R.id.target).text = System.currentTimeMillis().toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_projection)
        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        val textureView = findViewById<TextureView>(R.id.textureView)
        loopRun.run()

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val v = findViewById<View>(R.id.target)
                val arr = intArrayOf(0, 0)
                v.getLocationInWindow(arr)
                top = arr[1].toFloat()
                bottom = top + 200f
                glEnvironment = OpenGLEnvironment()
               // recorder = SurfaceToMedia(this@MediaProjectionActivity)

                val screenSize = screenRealSize()

                glEnvironment.createEnvironment(holder.surface) {
                    Texture.init(1)
                    GLES20.glViewport(0, 0, 200, 200)
                    GLES20.glClearColor(1f, 0f, 0f, 1f)
                    val loader = Loader()
                    val txture = BitmapTextureGLE(loader)
                    loader.load(R.raw.vertext, R.raw.luminance_frg)
                    surfaceTexture = SurfaceTexture(txture.id, false)
                    surfaceTexture.setDefaultBufferSize(screenSize.x, screenSize.y)
                    val txtSurface = Surface(surfaceTexture)

                    // val shader = LuminanceFilter(this@MediaProjectionActivity)

                    val v = floatArrayOf(
                        -1f, 1f, 0f,
                        1f, 1f, 0f,
                        -1f, -1f, 0f,
                        1f, -1f, 0f,
                    )
                    val rect = RectModelGLES(
                        v,
                        loader,
                    )
                    rect.texture = txture
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
                    capture.startCapture(txtSurface, screenSize.x, screenSize.y)
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
