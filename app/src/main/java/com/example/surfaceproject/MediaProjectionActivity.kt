package com.example.surfaceproject

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaceproject.glsl.Loader
import com.example.surfaceproject.model.RectModel
import com.example.surfaceproject.model.RectModelGLES
import com.example.surfaceproject.model.Triangle
import com.example.surfaceproject.texture.BitmapTextureGLES
import com.example.surfaceproject.texture.TextureGLES
import javax.microedition.khronos.opengles.GL10

class MediaProjectionActivity : AppCompatActivity() {
    private val capture = ScreenCapture(this)
    lateinit var glEnvironment: OpenGLEnvironment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_projection)
        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {

                // capture.startCapture(holder.surface, 200, 200)
                glEnvironment = OpenGLEnvironment()
                glEnvironment.createEnvironment(holder.surface) {
                    GLES20.glViewport(0 ,0, 200, 200)
                    GLES20.glClearColor(1f, 0f, 0f, 1f)
                    TextureGLES.init(1)
                    val surfaceTexture = SurfaceTexture(TextureGLES.tid[0], false)
                    val txtSurface = Surface(surfaceTexture)

                    capture.startCapture(txtSurface, 200, 200)
                    surfaceTexture.setOnFrameAvailableListener {
                        it.updateTexImage()
                    }
                    val txture = BitmapTextureGLES(0, null)
                    val loader = Loader()
                    loader.load(R.raw.vertext, R.raw.fragment)
                    val rect = RectModelGLES(
                        floatArrayOf(
                            -1f, 1f, 0f,
                            1f, 1f, 0f,
                            -1f, -1f, 0f,
                            1f, -1f, 0f,
                        ),
                        loader
                    )
                    //val rect = Triangle(loader)
                    // rect.texture = txture
                    it.draw {
                        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                        GLES20.glEnable(GLES20.GL_TEXTURE_2D)
                        // 设置为坐标系模式
                        // GLES20.glMatrixMode(GL10.GL_PROJECTION)
                        // 重置为单位矩阵，如果不重置的话，旋转平移等操作会影响后续操作
                        //GLES20.glLoadIdentity()
                        // 旋转
                        //gl.glRotatef(180f, 1f, 0f, 0f)

                        // 运行设置顶点数据

                        //gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
                        // 允许设置法向数据
                        //gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)
                        // 允许设置纹理坐标数据
                        // gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

                        rect.draw()
                    }
                }

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })
    }
}
