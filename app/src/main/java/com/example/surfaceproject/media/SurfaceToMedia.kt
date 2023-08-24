package com.example.surfaceproject.media

import android.content.Context
import android.media.MediaRecorder
import android.media.MediaRecorder.VideoEncoder
import android.os.Build
import android.view.Surface

/**
 * surface数据转换程媒体数据
 */
class SurfaceToMedia(context: Context, width: Int, height: Int, surface: Surface) {
    private val mediaRecorder: MediaRecorder

    init {
        /*val format = MediaFormat.createVideoFormat("video/mp4v-es", 200, 200)
        val encoderName = MediaCodecList(MediaCodecList.REGULAR_CODECS).findEncoderForFormat(format)
        val mediaCodec = MediaCodec.createByCodecName(encoderName)
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        MediaRecorder().
        MediaRecorder.VideoSource.SURFACE*/
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        // mediaRecorder.setVideoSize(width, height)
        mediaRecorder.setVideoEncoder(VideoEncoder.MPEG_4_SP)
        mediaRecorder.setVideoFrameRate(30)
        mediaRecorder.setVideoEncodingBitRate(30)
        //mediaRecorder.setInputSurface(surface)
        mediaRecorder.setOutputFile(context.externalCacheDir!!.absolutePath + "/1.mp4")
        mediaRecorder.prepare()
    }

    fun surface() = mediaRecorder.surface

    fun start() {
        mediaRecorder.start()
    }

    fun pause() {
        mediaRecorder.pause()
    }

    fun resume() {
        mediaRecorder.resume()
    }

    fun stop() {
        mediaRecorder.stop()
    }

    fun release() {
        mediaRecorder.release()
    }
}
