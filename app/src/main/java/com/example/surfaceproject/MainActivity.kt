package com.example.surfaceproject

import android.content.Context
import android.graphics.RectF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaceproject.gl.util.screenRealSize
import com.example.surfaceproject.pick.UiPagePick
import com.example.surfaceproject.record.ScreenCaptureInitialize
import com.fxf.debugwindowlibaray.ViewDebugManager

class MainActivity : AppCompatActivity() {

    private val screenRecord = ScreenCaptureInitialize(this)

    companion object {
        var ctx: Context? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ctx = this
        screenRecord.init {
            if (!it) return@init
            val pickPage = UiPagePick(screenRecord.getCore())
            ViewDebugManager.addPage(pickPage)
        }
    }

    override fun onPause() {
        super.onPause()
        // recorder.pause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        // recorder.resume()
    }
}
