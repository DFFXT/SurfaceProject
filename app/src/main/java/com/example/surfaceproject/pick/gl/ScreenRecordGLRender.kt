package com.example.surfaceproject.pick.gl

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.example.surfaceproject.gl.OpenGLEnvironment
import com.example.surfaceproject.gl.glsl.RectOESLoader
import com.example.surfaceproject.gl.graph.model.RectModel
import com.example.surfaceproject.gl.graph.texture.OESTexture
import com.example.surfaceproject.gl.util.captureScreenshot
import com.example.surfaceproject.gl.util.screenRealSize


class ScreenRecordGLRender {
    private val tag = "ScreenRecordGLRender"
    private lateinit var surfaceTexture: SurfaceTexture
    lateinit var surface: Surface

    // 这里暂时默认1920*1080
    var width: Int = 1920
        private set
    var height: Int = 1080
        private set

    private var previewWidth = width
    private var previewHeight = height
    private var paddingVertical = 0f
    private var paddingHorizontal = 0f
    private lateinit var glEnvironment: OpenGLEnvironment

    private lateinit var simpleTexture: OESTexture

    // 水印生成器
    // private var waterMarkGenerator: WaterMarkGenerator? = null



    /**
     * 用于预览的surface的组
     */
    private var previewSurfaceGroup: Int = 1

    private val waitingRunnable = ArrayList<Runnable>()
    private val captureScreenCallback = ArrayList<ScreenCaptureCallback>()

    /**
     * 创建gl环境
     */
    fun create(surface: Surface?, isPreviewSurface: Boolean, onStart: Runnable) {
        if (this::glEnvironment.isInitialized) return
        glEnvironment = OpenGLEnvironment()
        val screenSize = screenRealSize()
        val group = if (isPreviewSurface) previewSurfaceGroup else OpenGLEnvironment.DEF_GROUP
        glEnvironment.createEnvironment(surface!!, group) {
            val loader = RectOESLoader()
            simpleTexture = OESTexture(loader)
            surfaceTexture = SurfaceTexture(simpleTexture.id, false)
            surfaceTexture.setDefaultBufferSize(screenSize.x, screenSize.y)
            this.surface = Surface(surfaceTexture)
            // 执行等待任务
            if (waitingRunnable.isNotEmpty()) {
                for (runnable in waitingRunnable) {
                    runnable.run()
                }
                waitingRunnable.clear()
            }

            val v = floatArrayOf(
                -1f, 1f, 0f,
                1f, 1f, 0f,
                -1f, -1f, 0f,
                1f, -1f, 0f,
            )
            val rect = RectModel(v)
            rect.texture = simpleTexture





            GLES20.glClearColor(1f, 1f, 1f, 1f)
            // 关闭深度测试
            GLES20.glDisable(GLES20.GL_DEPTH_TEST)
            fun d() {
                it.draw(OpenGLEnvironment.DEF_GROUP, {

                    //GLES20.glEnable(GLES20.GL_TEXTURE_2D)
                    //GLES20.glEnable(GLES11Ext.GL_TEXTURE_EXTERNAL_OES)
                    // 恢复渲染区域
                    GLES20.glViewport(0, 0, width, height)
                    rect.draw()
                    // 绘制水印
                    // waterMarkGenerator?.drawOnGL(width, height)
                    // 执行截图任务
                    if (captureScreenCallback.isNotEmpty()) {
                        val bitmap = captureScreenshot(width, height)
                        for (callback in captureScreenCallback) {
                            if (callback.handler == null) {
                                callback.onImageCaptured(bitmap)
                            } else {
                                callback.handler.post {
                                    callback.onImageCaptured(bitmap)
                                }
                            }
                        }
                        captureScreenCallback.clear()
                    }
                })
                it.draw(previewSurfaceGroup) {
                    // 设置预览区域（包含缩放规则）
                    GLES20.glViewport(0, 0, previewWidth, previewHeight)
                    rect.texture?.padding(left = paddingHorizontal,
                        right = paddingHorizontal , top = paddingVertical, bottom = paddingVertical)
                    rect.draw()
                    // 重置padding，防止影响到录制
                    rect.texture?.resetPadding()
                    GLES20.glViewport(0, 0, width, height)
                }
            }

            surfaceTexture.setOnFrameAvailableListener({
                try {
                    it.updateTexImage()
                    d()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }, Handler(Looper.myLooper()!!))
            // waterMarkGenerator?.startGenerate()
            onStart.run()

        }
    }

    /*private fun createRectModel():RectModel {
        val v = floatArrayOf(
            -1f, 1f, 0f,
            1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f,
        )
          val gridBitmap = App.application.resources.getDrawable(R.mipmap.grid_1920x1080_with_coords) as BitmapDrawable
            val gridTexture = BitmapTexture(Rect2dLoader(), gridBitmap.bitmap)
            val gridRect = RectModel(v)
            gridTexture.prepare()
            gridRect.texture = gridTexture
        return gridRect
    }*/

    /**
     * 截屏
     */
    fun captureGlScreen(callback: ScreenCaptureCallback) {
        runOnGLThread {
            // 需要将截图放在渲染之后执行，否则有可能抓到空的数据
            captureScreenCallback.add(callback)
            /*val bitmap = captureScreenshot(width, height)
            if (handler == null) {
                callback(bitmap)
            } else {
                handler.post {
                    callback(bitmap)
                }
            }*/
        }
    }


    fun setPadding(left: Float, top: Float, right: Float, bottom: Float) {
        simpleTexture.padding(left, top, right, bottom)

    }

    fun bindSurface(surface: Surface) {
        if (::glEnvironment.isInitialized) {
            glEnvironment.bindSurface(OpenGLEnvironment.DEF_GROUP, surface)
        }
    }

    fun removeSurface(surface: Surface) {
        glEnvironment.removeSurface(OpenGLEnvironment.DEF_GROUP, surface)
    }
    /**
     * 预览的surface，为了防止一些时序问题以及预览延迟，预览数据现在统一从opengl中获取，因为eglSwapBuffers基本无延迟，而且不需要切换CameraRequest
     */
    fun bindPreviewSurface(surface: Surface) {
        if (::glEnvironment.isInitialized) {
            glEnvironment.bindSurface(previewSurfaceGroup, surface)
        }
    }

    fun removePreviewSurface(surface: Surface) {
        if (::glEnvironment.isInitialized) {
            glEnvironment.removeSurface(previewSurfaceGroup, surface)
        }
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
    /**
     * 更新预览尺寸
     */
    fun onPreviewSurfaceSizeChanged(previewWidth: Int, previewHeight: Int) {
        waitUntilSurfaceCreated {
            runOnGLThread {
                this.previewWidth = previewWidth
                this.previewHeight = previewHeight

                if (previewWidth / previewHeight.toFloat() > width / height.toFloat()) {
                    val k = width / previewWidth.toFloat()
                    val pw = width
                    val ph = previewHeight * k
                    paddingVertical = ((height - ph) / 2) / height
                    paddingHorizontal = 0f
                } else if (previewWidth / previewHeight.toFloat() < width / height.toFloat()) {
                    val k = height / previewHeight.toFloat()
                    val pw = previewWidth * k
                    val ph = height
                    paddingVertical = 0f
                    paddingHorizontal = ((width - pw) / 2) / width
                } else {
                    paddingVertical = 0f
                    paddingHorizontal = 0f
                }
            }
        }
        Log.d(tag, "onPreviewSurfaceSizeChanged: $previewWidth $previewHeight")
    }

    /**
     * 等待创建成功
     */
    fun waitUntilSurfaceCreated(runnable: Runnable) {
        if (::surface.isInitialized) {
            runnable.run()
        } else {
            waitingRunnable.add(runnable)
        }
    }

    fun release() {
        runOnGLThread {
            surface.release()
            surfaceTexture.release()
            glEnvironment.release()
        }
    }

    abstract class ScreenCaptureCallback(val handler: Handler? = null) {
        abstract fun onImageCaptured(bitmap: Bitmap)
    }
}
