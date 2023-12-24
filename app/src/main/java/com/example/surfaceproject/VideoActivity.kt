package com.example.surfaceproject

import android.content.res.AssetManager
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaceproject.media.decode.MediaToSurface
import java.nio.ByteBuffer

class VideoActivity: AppCompatActivity() {

    companion object {
        var asset: AssetManager? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        asset = this.assets
        val surface = findViewById<SurfaceView>(R.id.video)
        surface.holder.addCallback(object : SurfaceHolder.Callback {
            private val mediaToSurface = MediaToSurface()
            override fun surfaceCreated(holder: SurfaceHolder) {
                mediaToSurface.setSurface(holder.surface)
                val extractor = MediaExtractor()
                extractor.setDataSource(assets.openFd("test.mp4"))
                for (i in 0 until extractor.trackCount) {
                    val format = extractor.getTrackFormat(i)
                    extractor.selectTrack(0)
                }
                mediaToSurface.init(MediaFormat.MIMETYPE_VIDEO_AVC, MediaFormat.MIMETYPE_VIDEO_AVC,960,544)

                mediaToSurface.start()
               /* var len = 0
                while (true) {
                    val buffer = ByteBuffer.allocate(960*544*10)
                    len = extractor.readSampleData(buffer,0)
                    if (len < 0) {
                        break
                    }
                    mediaToSurface.decode(buffer, 0, len, extractor.sampleTime)
                    extractor.advance()
                }*/
               /* videoStream.use {
                    val buffer = ByteArray(10240)
                    var size = it.read(buffer)
                    while (size > 0) {
                        mediaToSurface.decode(buffer, 0, size)
                        size = it.read(buffer)
                    }
                }*/

            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                mediaToSurface.setSurface(holder.surface)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mediaToSurface.setSurface(null)
            }

        })
    }
}