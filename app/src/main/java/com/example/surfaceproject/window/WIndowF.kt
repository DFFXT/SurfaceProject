package com.example.surfaceproject.window

import android.view.WindowManager
import androidx.core.content.getSystemService
import com.example.surfaceproject.App

class WIndowF {


    fun show() {
        val wm = App.ctx.getSystemService<WindowManager>()
    }
}