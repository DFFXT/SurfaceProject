package com.mx.screenshot

import android.graphics.Bitmap
import android.media.ImageReader
import java.io.FileOutputStream

class ScreenCapture(private val recorder: ScreenRecord) {
    private val imageReader by lazy { ImageReader.newInstance(1920, 720, 0x1, 2) }

    fun init() {
        recorder.startCapture(imageReader.surface, 1920, 720)
    }

    fun capture(callback: (Bitmap) -> Unit) {
        /*imageReader.setOnImageAvailableListener({

        }, null)*/
        imageReader.setOnImageAvailableListener(null, null)
        val image = imageReader.acquireLatestImage()
        val width = image.getWidth()
        val height = image.getHeight()
        val planes = image.getPlanes()

        val buffer = planes[0].getBuffer()
        val pixelStride = planes[0].getPixelStride()
        val rowStride = planes[0].getRowStride()
        val rowPadding = rowStride - pixelStride * width
        val bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        callback.invoke(bitmap)
        image.close()
    }

    fun save(path: String) {
        capture { bitmap ->
            FileOutputStream(path).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.flush()
            }
        }
    }
}