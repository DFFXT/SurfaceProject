package com.example.surfaceproject.media.decode

import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.view.Surface
import com.example.surfaceproject.VideoActivity
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class MediaToSurface: Thread("MediaToSurface") {
    private var surface: Surface? = null
    private lateinit var decoder: MediaCodec
    private val dataList = LinkedBlockingQueue<Frame>()
    private var destroy = false
    val extractor = MediaExtractor()

    /**
     * [MediaFormat.MIMETYPE_VIDEO_MPEG4]
     */
    fun init(type: String, videoMime: String, width: Int, height: Int) {
        // val list = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val format = MediaFormat.createVideoFormat(videoMime, width, height)
        /*val decoderName = list.findDecoderForFormat(format)*/
        extractor.setDataSource(VideoActivity.asset!!.openFd("test.mp4"))
        val fm = extractor.getTrackFormat(0)
        val f = extractor.selectTrack(0)
        decoder = MediaCodec.createDecoderByType(type)
        decoder.configure(fm, surface, null, 0)
        decoder.start()
    }
    fun setSurface(surface: Surface?) {
        this.surface = surface
        if (::decoder.isInitialized && surface != null) {
            decoder.setOutputSurface(surface)
        }
        if (surface == null) {
        }
    }

    fun decode(byteArray: ByteBuffer, offset: Int, length: Int, time: Long) {
        dataList.offer(Frame(byteArray, offset, length, time))
    }

    fun release() {
        destroy = true
    }

    override fun run() {
        // val buffer = MediaCodec.BufferInfo()
        val outBuffer = MediaCodec.BufferInfo()

        extractor.selectTrack(0)
        while (true) {

            val index = decoder.dequeueInputBuffer(-1)
            if (index >= 0) {
                val buffer = decoder.getInputBuffer(index) ?: continue
                val size = extractor.readSampleData(buffer, 0)
                if (size == -1) return
                decoder.queueInputBuffer(index, 0, size, extractor.sampleTime, 0)

                val i = decoder.dequeueOutputBuffer(outBuffer, 0)
                if (i >= 0) {
                    decoder.releaseOutputBuffer(i, true)
                }
                extractor.advance()
                sleep(30)
            }
        }

        while (true) {
            val frame = dataList.poll(1000, TimeUnit.MILLISECONDS)
            if (frame == null) {
                if (destroy) {
                    break
                }
                continue
            }
            var consumedSize = 0
            while (consumedSize != frame.length) {
                var index = decoder.dequeueInputBuffer(-1)
                while (index < 0) {
                    index = decoder.dequeueInputBuffer(-1)
                }
                val buffer = decoder.getInputBuffer(index)!!
                val start = buffer.position()
                val avilableSize = buffer.capacity() - start
                val remainSize = frame.length - consumedSize

                val putSize = minOf(avilableSize, remainSize)
                buffer.put(frame.data.array(), frame.start + consumedSize, putSize)
                decoder.queueInputBuffer(index, start, putSize, frame.time, 0)
                consumedSize += putSize
            }
            Thread.sleep(30)


            val index = decoder.dequeueOutputBuffer(outBuffer, 0)
            if (index >= 0) {
                decoder.releaseOutputBuffer(index, true)
            }

        }
        decoder.stop()
        decoder.release()
    }

    class Frame(val data: ByteBuffer, val start: Int, val length: Int, val time: Long)
}