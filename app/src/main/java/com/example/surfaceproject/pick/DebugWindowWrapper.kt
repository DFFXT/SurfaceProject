package com.example.surfaceproject.pick

import android.app.Application
import com.example.surfaceproject.pick.page.UiPageVideo
import com.fxf.debugwindowlibaray.ViewDebugManager

object DebugWindowWrapper {
    fun init(app: Application) {
        ViewDebugManager.init(app)
        ViewDebugManager.overOtherApplication(true)
        // ViewDebugManager.addPage(UiPagePick())
        ViewDebugManager.addPage(UiPageVideo())
    }
}