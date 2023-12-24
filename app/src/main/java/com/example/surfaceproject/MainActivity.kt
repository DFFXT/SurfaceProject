package com.example.surfaceproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.surfaceproject.permission.IOPermission
import com.example.surfaceproject.pick.page.UiPagePick
import com.example.surfaceproject.media.record.ScreenCaptureInitialize
import com.fxf.debugwindowlibaray.ViewDebugManager

class MainActivity : AppCompatActivity() {

    private val screenRecord = ScreenCaptureInitialize(this)
    private val ioPermission = IOPermission(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ioPermission.request { ioGranted ->
            if (ioGranted) {
                screenRecord.init {
                    if (it) {
                        val pickPage = UiPagePick(screenRecord.getCore())
                        ViewDebugManager.addPage(pickPage)
                    } else {
                        finish()
                    }
                }
            } else {
                finish()
            }
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
        Log.i("ssssf", "resume$this")
        // recorder.resume()
    }
}
