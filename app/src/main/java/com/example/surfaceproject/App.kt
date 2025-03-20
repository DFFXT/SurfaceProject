package com.example.surfaceproject

import android.app.Application
import android.content.Intent
import com.example.surfaceproject.pick.DebugWindowWrapper
import com.fxffxt.preferen.Config

class App: Application() {
    companion object {
        lateinit var ctx: Application
    }
    override fun onCreate() {
        super.onCreate()
        ctx = this
        DebugWindowWrapper.init(this)
        startForegroundService(Intent(this, ForegroundService::class.java))
    }
}
