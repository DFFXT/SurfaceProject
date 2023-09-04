package com.example.surfaceproject.record

import android.content.Context
import android.media.MediaRecorder
import android.media.MediaRecorder.VideoEncoder
import android.os.Build
import android.os.ParcelFileDescriptor
import android.view.Surface
import com.example.surfaceproject.pick.storage.VideoQuery
import com.example.surfaceproject.pick.storage.VideoConfig

/**
 * surface数据转换程媒体数据
 */
class SurfaceToMedia(private val context: Context) {
    private lateinit var mediaRecorder: MediaRecorder
    private var surface: Surface? = null
    private lateinit var pfd: ParcelFileDescriptor
    private lateinit var videoName: String

    fun prepare(width: Int, height: Int) {
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
        // 通过AFS访问媒体文件
        videoName = "screenCapture_video_" + System.currentTimeMillis() + ".mp4"
        val uri = VideoQuery.createVideo(videoName)
        pfd = context.contentResolver.openFileDescriptor(uri, "rw")!!
        mediaRecorder.setOutputFile(pfd.fileDescriptor)
        mediaRecorder.prepare()
        surface = mediaRecorder.surface
    }

    fun surface() = surface

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
        pfd.close()
        VideoConfig.addVideoItem(VideoConfig.VideoItem(videoName))
    }

    fun release() {
        mediaRecorder.release()
        surface?.release()
    }
}
