package com.example.surfaceproject.pick

import android.content.Context
import android.graphics.RectF
import android.view.Surface
import com.example.surfaceproject.App
import com.example.surfaceproject.gl.util.screenRealSize
import com.example.surfaceproject.pick.gl.ScreenRecordGLRender
import com.example.surfaceproject.media.record.ScreenCaptureCore
import com.example.surfaceproject.media.record.SurfaceToMedia

/**
 * 录屏部分区域转mp4
 */
class ScreenRecordManager {
    private lateinit var recorder: SurfaceToMedia
    private val render = ScreenRecordGLRender()
    private lateinit var capture: ScreenCaptureCore
    private lateinit var ctx: Context
    private var previewSurface: Surface? = null

    fun init(core: ScreenCaptureCore, context: Context) {
        this.capture = core
        this.ctx = context
    }

    /**
     * 设置预览surface
     */
    fun setPreviewSurface(surface: Surface) {
        previewSurface = surface
        render.bindSurface(surface)
    }

    fun removePreviewSurface(surface: Surface) {
        render.removeSurface(surface)
    }

    /**
     * 环境准备
     * @return true 成功准备
     */
    fun prepare(rectF: RectF): Boolean {
        // 准备surface到媒体工具
        try {
            // 经测试，在雷电模拟器上，如果视频尺寸太小会直接IOException
            reset()
            recorder = SurfaceToMedia(App.ctx)
            recorder.prepare(rectF.width().toInt(), rectF.height().toInt())
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        // 准备opengl环境
        val size = screenRealSize()
        val width = size.x
        val height = size.y

        render.create(recorder.surface()!!, false) {}
        // 设置预览
        previewSurface?.let {
            render.bindSurface(it)
        }
        render.runOnGLThread {
            capture.release()
            capture.start(width, height, ctx.resources.displayMetrics.densityDpi)
            // gl环境创建成功，输出纹理到opengl
            capture.setSurface(render.surface)
            // 设置图像保留区域
            render.setPadding(rectF.left / width, rectF.top / height, 1 - rectF.right / width, 1 - rectF.bottom / height)
            render.onSurfaceSizeChanged(rectF.width().toInt(), rectF.height().toInt())
        }
        return true
    }

    /**
     * 开始录制
     * @param rectF,屏幕录制区域
     */
    fun startRecord() {
        // gl输出到surface
        render.bindSurface(recorder.surface()!!)
        recorder.start()
    }

    fun pause() {
        recorder.pause()
    }

    fun resume() {
        recorder.pause()
    }

    fun stop() {
        recorder.stop()
        reset()
    }

    fun release() {
        // capture.release()
        recorder.release()
        render.release()
    }

    private fun reset() {
        if (::recorder.isInitialized) {
            recorder.surface()?.let {
                render.removeSurface(it)
            }
            recorder.release()
        }
    }
}
