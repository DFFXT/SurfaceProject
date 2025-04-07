package com.example.surfaceproject.gl

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import java.util.LinkedList

/**
 * 构建一个OpenGL环境
 */
class OpenGLEnvironment {
    companion object {
        val DEF_GROUP = 0
        val ALL_GROUP = -1
    }
    lateinit var eglDisplay: EGLDisplay
    // 对surface进行分组
    private val eglSurfaceList = HashMap<Int, LinkedList<Pair<Surface, EGLSurface>>>()

    // private lateinit var eglSurface: EGLSurface
    lateinit var eglContext: EGLContext
    private lateinit var eglConfig: EGLConfig
    private lateinit var eglThread: Thread
    private lateinit var handler: Handler
    private val actions = LinkedList<Runnable>()

    /**
     * @param surface，图像输出 todo 其实这里不传surface更合理些
     * @param shareEGLContext 共享context
     * @param runnable 环境构建成功回调
     */
    fun createEnvironment(surface: Surface, groupId: Int, shareEGLContext: EGLContext? = null, runnable: (OpenGLEnvironment) -> Unit) {
        if (this::eglDisplay.isInitialized) return
        // 绑定surface，会在gl环境创建好后执行真正的绑定
        /*if (surface != null) {
            bindSurface(surface)
        }*/
        // 创建EglDisplay
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)

        // eglDisplay需要初始化，否则后台启动会报：validate_display:93 error 3001 (EGL_NOT_INITIALIZED)
        val r = EGL14.eglInitialize(eglDisplay, null, 0, null, 0)
        Log.d("OpenGLEnvironment", "eglDisplay: $eglDisplay $r")

        // EGL14.eglGetConfigs(display, null, 0, 0, configNumber, 0)

        // EGL14.eglGetConfigs(display, configs, 0, configs.size, configNumber, 0)
        val attrList = intArrayOf(
            EGL14.EGL_RED_SIZE,
            8,
            EGL14.EGL_GREEN_SIZE,
            8,
            EGL14.EGL_BLUE_SIZE,
            8,
            EGL14.EGL_ALPHA_SIZE,
            8,
            EGL14.EGL_NONE,
        )
        // 获取config信息
        val configNumber = IntArray(1)
        EGL14.eglChooseConfig(eglDisplay, attrList, 0, null, 0, 0, configNumber, 0)
        // 确定该配置下的数量
        if (configNumber[0] != 0) {
            val configs = Array<EGLConfig?>(configNumber[0]) { null }
            // 获取config对象
            EGL14.eglChooseConfig(eglDisplay, attrList, 0, configs, 0, configs.size, configNumber, 0)

            // 通过config，取第一个创建EglSurface对象
            eglConfig = configs[0]!!
            val eglSurface = createEglSurface(surface)
            var group = eglSurfaceList[groupId]
            if (group == null) {
                group = LinkedList()
                eglSurfaceList[groupId] = group
            }
            group.add(Pair(surface, eglSurface))
            // 创建EglContext对象
            eglContext = shareEGLContext ?: EGL14.eglCreateContext(eglDisplay, configs[0]!!, EGL14.EGL_NO_CONTEXT, getGlVersion(), 0)
            Log.d("OpenGLEnvironment", "configNumber: 1")
            eglThread = Thread {
                Log.d("OpenGLEnvironment", "eglThread: enter")
                // 将当前线程绑定到EglContext，和Egl关联气来，这个显示就是gl线程了
                EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
                Looper.prepare()
                handler = Handler(Looper.myLooper()!!)
                runnable(this)
                actions.forEach {
                    it.run()
                }
                actions.clear()
                Log.d("OpenGLEnvironment", "eglThread: loop")
                Looper.loop()
            }
            eglThread.start()
        } else {
            Log.d("OpenGLEnvironment", "configNumber: 0")
        }
    }

    private fun getGlVersion(): IntArray {
        return intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION,
            2,
            EGL14.EGL_NONE,
        )
    }

    private fun createEglSurface(surface: Surface): EGLSurface {
        return EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, null, 0)
    }

    /**
     * 绑定surface
     */
    fun bindSurface(group: Int, surface: Surface) {
        post {
            var surfaceGroup = eglSurfaceList[group]
            if (surfaceGroup?.find { it.first == surface } == null) {
                val eglSurface = createEglSurface(surface)
                EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
                if (surfaceGroup == null) {
                    surfaceGroup = LinkedList()
                    eglSurfaceList[group] = surfaceGroup
                }
                surfaceGroup.add(Pair(surface, eglSurface))
            }
        }
    }

    fun removeSurface(group: Int, vararg surfaces: Surface) {
        post {
            val surfaceGroup = eglSurfaceList[group] ?: return@post
            surfaces.forEach { surface ->
                val pair = surfaceGroup.find { it.first == surface } ?: return@post
                surfaceGroup.remove(pair)
                EGL14.eglDestroySurface(eglDisplay, pair.second)
            }
        }
    }

    /**
     * 绘制
     * @param runnable 要绘制的操作，会立即交换缓冲区
     */
    fun draw(group: Int, runnable: Runnable) {
        post {
            if (group != ALL_GROUP) {
                val surfaceGroup = eglSurfaceList[group] ?: return@post
                surfaceGroup.forEach {
                    EGL14.eglMakeCurrent(eglDisplay, it.second, it.second, eglContext)
                    runnable.run()
                    val error = GLES20.glGetError()
                    if (error != GLES20.GL_NO_ERROR) {
                        Log.d("GLERROR","error: $error")
                    }
                    EGL14.eglSwapBuffers(eglDisplay, it.second)
                }
            } else {
                eglSurfaceList.forEach { surfaceGroup ->
                    surfaceGroup.value.forEach {
                        EGL14.eglMakeCurrent(eglDisplay, it.second, it.second, eglContext)
                        runnable.run()
                        val error = GLES20.glGetError()
                        if (error != GLES20.GL_NO_ERROR) {
                            // Log.d("GLERROR","error: $error")
                        }
                        EGL14.eglSwapBuffers(eglDisplay, it.second)
                    }
                }
            }

        }
    }


    /**
     * 将代码送到gl线程执行
     * @param runnable action
     */
    fun post(runnable: Runnable) {
        if (!this::handler.isInitialized) {
            actions.add(runnable)
        } else {
            if (Thread.currentThread() == eglThread) {
                runnable.run()
            } else {
                handler.post(runnable)
            }
        }
    }

    /**
     * 释放
     */
    fun release() {
        eglSurfaceList.forEach {
            removeSurface(it.key, *it.value.map { it.first }.toTypedArray())
        }
        post {
            eglThread.interrupt()
            Looper.myLooper()?.quitSafely()
        }
    }
}
