package com.mx.screenshot

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.skin.skincore.SkinManager

class ScreenRecord(private val activity: ComponentActivity) {
    private var launcher: ActivityResultLauncher<Intent>
    private lateinit var mediaProjection: MediaProjection
    private lateinit var surface: Surface
    private var width: Int = 0
    private var height: Int = 0

    private val manager by lazy {
        activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    init {
        launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            mediaProjection = manager.getMediaProjection(it.resultCode, it.data!!)
            start(width, height)
        }
    }

    fun startCapture(surface: Surface, width: Int, height: Int) {
        this.width = width
        this.height = height
        this.surface = surface
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
