package com.example.surfaceproject

import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.surfaceproject.gl.util.screenRealSize

class ScreenCapture(private val activity: ComponentActivity) {
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var mediaProjection: MediaProjection
    private lateinit var surface: Surface
    private var width: Int = 0
    private var height: Int = 0

    init {
        launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val manager = activity.getSystemService(MediaProjectionManager::class.java)
            mediaProjection = manager.getMediaProjection(it.resultCode, it.data!!)
            start(width, height)
        }
    }

    fun startCapture(surface: Surface, width: Int = screenRealSize().x, height: Int = screenRealSize().y, onStart: Runnable) {
        this.width = width
        this.height = height
        this.surface = surface
        val manager = activity.getSystemService(MediaProjectionManager::class.java)
        launcher.launch(manager.createScreenCaptureIntent())
    }

    private fun start(width: Int, height: Int) {
        val vDisplay = mediaProjection.createVirtualDisplay(
            "v-display",
            width,
            height,
            activity.resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            surface,
            object : VirtualDisplay.Callback() {
            },
            null,
        )
    }
}
