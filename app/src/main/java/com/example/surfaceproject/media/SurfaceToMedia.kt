package com.example.surfaceproject.media

import android.content.Context
import android.media.MediaRecorder
import android.media.MediaRecorder.VideoEncoder
import android.os.Build
import android.view.Surface

/**
 * surface数据转换程媒体数据
 */
class SurfaceToMedia(context: Context, width: Int, height: Int) {
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
        // 需要手动设置大小，否则输出视频显示异常
        mediaRecorder.setVideoSize(width, height)
        // 模拟器无法设置mp4的编码器
        mediaRecorder.setVideoEncoder(VideoEncoder.DEFAULT)
        mediaRecorder.setVideoFrameRate(30)
        // 设置比特率，即每秒处理和传输的字节数量
        mediaRecorder.setVideoEncodingBitRate(150000)
        // mediaRecorder.setPreviewDisplay(surface)
        // mediaRecorder.setInputSurface(surface)
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
