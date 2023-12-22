package com.example.surfaceproject.record

import android.content.Intent
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * 录屏初始化，主要为权限申请
 */
class ScreenCaptureInitialize(private val activity: ComponentActivity) {
    private lateinit var core: ScreenCaptureCore
    private val launcher: ActivityResultLauncher<Intent>
    private var mediaProjection: MediaProjection? = null

    private var onStartCallback: ((Boolean) -> Unit)? = null

    init {
        launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val manager = activity.getSystemService(MediaProjectionManager::class.java)
            mediaProjection = manager.getMediaProjection(it.resultCode, it.data!!)
            if (mediaProjection != null) {
                core = ScreenCaptureCore(mediaProjection!!)
                // core.start(width, height, activity.resources.displayMetrics.densityDpi)
            }
            this.onStartCallback?.invoke(mediaProjection != null)
        }
    }

    fun init(callback: ((Boolean) -> Unit)? = null) {
        this.onStartCallback = callback
        val manager = activity.getSystemService(MediaProjectionManager::class.java)
        launcher.launch(manager.createScreenCaptureIntent())
    }

    /**
     * 获取录屏核心，必须再init之后调研
     */
    fun getCore(): ScreenCaptureCore {
        return core
    }
}
