package com.example.surfaceproject.pick

import android.app.Application
import com.fxf.debugwindowlibaray.ViewDebugManager

object DebugWindowWrapper {
    fun init(app: Application) {
        ViewDebugManager.init(app)
        ViewDebugManager.addPage(UiPagePick())
    }
}