package com.example.surfaceproject

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.os.Handler
import android.os.Looper
import android.view.Surface

/**
 * 构建一个OpenGL环境
 */
class OpenGLEnvironment {
    private lateinit var eglDisplay: EGLDisplay
    private lateinit var eglSurface: EGLSurface
    private lateinit var eglContext: EGLContext
    private lateinit var eglThread: Thread
    private lateinit var handler: Handler

    fun createEnvironment(surface: Surface, runnable: (OpenGLEnvironment) -> Unit) {
        if (this::eglThread.isInitialized) return
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val configNumber = IntArray(1)
        // EGL14.eglGetConfigs(display, null, 0, 0, configNumber, 0)

        // EGL14.eglGetConfigs(display, configs, 0, configs.size, configNumber, 0)
        val attrList = intArrayOf(
            EGL14.EGL_RED_SIZE,
            8,
            EGL14.EGL_GREEN_SIZE,
            8,
            EGL14.EGL_BLUE_SIZE,
            8,
            EGL14.EGL_NONE,
        )
        EGL14.eglChooseConfig(eglDisplay, attrList, 0, null, 0, 0, configNumber, 0)
        if (configNumber[0] != 0) {
            val configs = Array<EGLConfig?>(configNumber[0]) { null }
            EGL14.eglChooseConfig(eglDisplay, attrList, 0, configs, 0, configs.size, configNumber, 0)

            eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, configs[0]!!, surface, null, 0)
            val glVersion = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION,
                2,
                EGL14.EGL_NONE,
            )
            eglContext = EGL14.eglCreateContext(eglDisplay, configs[0]!!, EGL14.EGL_NO_CONTEXT, glVersion, 0)
            eglThread = Thread {
                EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
                Looper.prepare()
                handler = Handler(Looper.myLooper()!!)
                runnable(this)
                Looper.loop()
            }
            eglThread.start()
        }
    }

    fun draw(runnable: Runnable) {
        post {
            runnable.run()
            EGL14.eglSwapBuffers(eglDisplay, eglSurface)
        }
    }

    fun post(runnable: Runnable) {
        handler.post(runnable)
    }
}
