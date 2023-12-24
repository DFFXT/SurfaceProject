package com.example.surfaceproject.media.record

import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.view.Surface

/**
 * 录屏核心代码
 */
class ScreenCaptureCore(private val mediaProjection: MediaProjection) {

    private lateinit var virtualDisplay: VirtualDisplay
    fun start(width: Int, height: Int, dpi: Int) {
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "v-display",
            width,
            height,
            dpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            null,
            object : VirtualDisplay.Callback() {
            },
            null,
        )
    }

    fun setSurface(surface: Surface) {
        virtualDisplay.surface = surface
    }

    fun removeSurface() {
        if (this::virtualDisplay.isInitialized) {
            virtualDisplay.surface = null
        }
    }

    fun release() {
        if (this::virtualDisplay.isInitialized) {
            virtualDisplay.surface = null
            virtualDisplay.release()
        }
    }
}
