package com.example.surfaceproject

import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaceproject.gl.graph.MainRender
import com.example.surfaceproject.gl.util.screenRealSize
import com.example.surfaceproject.media.SurfaceToMedia
import com.example.surfaceproject.pick.UiPagePick
import com.fxf.debugwindowlibaray.ViewDebugManager

class MediaProjectionActivity : AppCompatActivity() {
    private var recorder: SurfaceToMedia? = null
    private val capture = ScreenCapture(this)
    lateinit var glEnvironment: OpenGLEnvironment

    // SurfaceTexture 需要防止被回收
    lateinit var surfaceTexture: SurfaceTexture
    private val render = MainRender()
    private val pickPage = UiPagePick()
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
        val surfaceView2 = findViewById<SurfaceView>(R.id.surfaceView2)
        surfaceView2.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                return
                surfaceView2.post {
                    render.start(holder.surface) {
                        val size = screenRealSize()
                        /*capture.startCapture(render.surface, size.x, size.y) {
                        }*/
                    }
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
        loopRun.run()
        ViewDebugManager.addPage(pickPage)
        pickPage.addPickResultListener(object : UiPagePick.PickListener {

            val size = screenRealSize()
            val width = size.x
            val height = size.y
            override fun onResult(rectF: RectF) {
                render.setPadding(rectF.left / width, rectF.top / height, 1 - rectF.right / width, 1 - rectF.bottom / height)
                // render.setPadding(0, )
            }
        })

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                recorder = SurfaceToMedia(this@MediaProjectionActivity)
                render.start(recorder!!.surface()) {
                    val size = screenRealSize()
                    capture.startCapture(render.surface, size.x, size.y) {

                    }

                    // render.bindSurface(recorder!!.surface())
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

    override fun onDestroy() {
        super.onDestroy()
        ViewDebugManager.removePage(pickPage)
    }
}
