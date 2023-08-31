package com.example.surfaceproject

import android.app.Application
import com.example.surfaceproject.pick.DebugWindowWrapper
import com.fxffxt.preferen.Config

class App: Application() {
    companion object {
        lateinit var ctx: Application
    }
    override fun onCreate() {
        super.onCreate()
        ctx = this
        Config.ctx = this
        DebugWindowWrapper.init(this)
    }
}
