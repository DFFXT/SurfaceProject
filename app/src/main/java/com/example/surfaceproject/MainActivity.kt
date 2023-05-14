package com.example.surfaceproject

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.TextureView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaceproject.model.RectModel
import com.example.surfaceproject.texture.BitmapTexture
import com.example.surfaceproject.texture.Texture
import toBitmap
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

fun FloatArray.toBuffer(): FloatBuffer {
    val b = ByteBuffer.allocateDirect(size * 4)
    b.order(ByteOrder.nativeOrder())
    val buffer = b.asFloatBuffer()
    buffer.put(this)
    buffer.position(0)
    return buffer
}

class MainActivity : AppCompatActivity() {
    private val bitmapTexture = BitmapTexture(0 , R.mipmap.icon_local.toBitmap())
    private val bitmapTextureBg = BitmapTexture(1, R.mipmap.icon_bg.toBitmap())
    private val rect = RectModel(
        floatArrayOf(
            -1f, 1f, 0f,
            1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f,
        ),
    )
    private val rect1 = RectModel(
        floatArrayOf(
            -1f, 1f, 0f,
            1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f,
        ),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rect.texture = bitmapTexture
        rect1.texture = bitmapTextureBg
        setContentView(R.layout.activity_main)
        // val glSurfaceView: GLSurfaceView = findViewById(0)

        val textureView: TextureView = findViewById(R.id.textureView)
        val glSurfaceView: GLSurfaceView = findViewById(R.id.glSurfaceView)
        val left: EditText = findViewById(R.id.tv_left)
        val right: EditText = findViewById(R.id.tv_right)

        // 创建图片纹理
// Create texture

        // val myTexture = SurfaceTexture(tid[0])

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int,
            ) {
                // surface.attachToGLContext(tid[0])
                /* val surface = Surface(surface)
                 val c = surface.lockHardwareCanvas()
                 c.drawColor(Color.RED)
                 surface.unlockCanvasAndPost(c)*/
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int,
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }
        }
        glSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
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
        glSurfaceView.setRenderer(object : GLSurfaceView.Renderer {
            override fun onSurfaceCreated(
                gl: GL10,
                config: javax.microedition.khronos.egl.EGLConfig?,
            ) {
                // textures[0] = loadTexture(gl, R.mipmap.icon_default_head)
                Texture.init(gl, 2)
                bitmapTexture.load(gl)
                bitmapTextureBg.load(gl)


                gl.glClearColor(0f, 0f, 0f, 0f)
            }

            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            }

            val src = intArrayOf(
                GL10.GL_ZERO,
                GL10.GL_ONE,
                GL10.GL_SRC_ALPHA,
                GL10.GL_DST_ALPHA,
                GL10.GL_DST_COLOR,
                GL10.GL_SRC_COLOR,
                GL10.GL_ONE_MINUS_SRC_ALPHA,
                GL10.GL_ONE_MINUS_DST_ALPHA,
                GL10.GL_ONE_MINUS_SRC_COLOR,
                GL10.GL_ONE_MINUS_DST_COLOR,
            )
            override fun onDrawFrame(gl: GL10) {
                // 设置背景颜色填充，glClearColor设置的值
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
                // 允许2d贴图
                gl.glEnable(GL10.GL_TEXTURE_2D)
                // 设置为坐标系模式
                gl.glMatrixMode(GL10.GL_PROJECTION)
                // 重置为单位矩阵，如果不重置的话，旋转平移等操作会影响后续操作
                gl.glLoadIdentity()
                // 旋转
                gl.glRotatef(180f, 1f, 0f, 0f)

                // 运行设置顶点数据
                gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
                // 允许设置法向数据
                gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)
                // 允许设置纹理坐标数据
                gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)


                rect.draw(gl)
                gl.glEnable(GL10.GL_BLEND)
                try {
                    val l = left.text.toString().toInt()
                    val r = right.text.toString().toInt()
                    gl.glBlendFunc(src[l], src[r])
                }catch (e: Exception) {

                }


                //gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
                rect1.draw(gl)
                gl.glDisable(GL10.GL_BLEND)
                // 设置三角形组坐标
                /*gl.glVertexPointer(3, GL10.GL_FLOAT, 0, rect.vertexBuffer)
                // 设置三角形法向
                gl.glNormalPointer(GL10.GL_FLOAT, 0, fx.toBuffer())*/
                // 设置纹理坐标
                // gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureSqure.toBuffer())
                // 绘制，将三角坐标和纹理坐标对应
                // gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4)
                gl.glFlush()
            }
        })
    }
}
