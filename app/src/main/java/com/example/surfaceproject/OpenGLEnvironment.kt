package com.example.surfaceproject

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.os.Handler
import android.os.Looper
import android.view.Surface
import java.util.LinkedList

/**
 * 构建一个OpenGL环境
 */
class OpenGLEnvironment {
    private lateinit var eglDisplay: EGLDisplay
    private val eglSurfaceList = LinkedList<Pair<Surface, EGLSurface>>()

    // private lateinit var eglSurface: EGLSurface
    lateinit var eglContext: EGLContext
    private lateinit var eglConfig: EGLConfig
    private lateinit var eglThread: Thread
    private lateinit var handler: Handler

    /**
     * @param surface，图像输出
     * @param shareEGLContext 共享context
     * @param runnable 环境构建成功回调
     */
    fun createEnvironment(surface: Surface, shareEGLContext: EGLContext? = null, runnable: (OpenGLEnvironment) -> Unit) {
        // if (this::eglThread.isInitialized) return
        // 创建EglDisplay
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)

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
            eglSurfaceList.add(Pair(surface, eglSurface))
            // 创建EglContext对象
            eglContext = shareEGLContext ?: EGL14.eglCreateContext(eglDisplay, configs[0]!!, EGL14.EGL_NO_CONTEXT, getGlVersion(), 0)
            eglThread = Thread {
                // 将当前线程绑定到EglContext，和Egl关联气来，这个显示就是gl线程了
                EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
                Looper.prepare()
                handler = Handler(Looper.myLooper()!!)
                runnable(this)
                Looper.loop()
            }
            eglThread.start()
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
     * 绑定surface，会移除之前的surface
     */
    fun bindSurface(surface: Surface) {
        post {
            if (eglSurfaceList.find { it.first == surface } == null) {
                val eglSurface = createEglSurface(surface)
                EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
                eglSurfaceList.add(Pair(surface, eglSurface))
            }
        }
    }

    fun removeSurface(surface: Surface) {
        post {
            val pair = eglSurfaceList.find { it.first == surface } ?: return@post
            EGL14.eglDestroySurface(eglDisplay, pair.second)
        }
    }

    /**
     * 绘制
     * @param runnable 要绘制的操作，会立即交换缓冲区
     */
    fun draw(runnable: Runnable) {
        post {
            // runnable.run()
            eglSurfaceList.forEach {
                EGL14.eglMakeCurrent(eglDisplay, it.second, it.second, eglContext)
                runnable.run()
                EGL14.eglSwapBuffers(eglDisplay, it.second)
            }
            /*            val it = eglSurfaceList.getOrNull(1) ?: eglSurfaceList.get(0)
                        val first = eglSurfaceList.get(0)
                        EGL14.eglMakeCurrent(eglDisplay, first.second, first.second, eglContext)
                        //EGL14.eglSwapBuffers(eglDisplay, first.second)
                        val second = eglSurfaceList.getOrNull(1)
                        if (second != null) {
                            EGL14.eglMakeCurrent(eglDisplay, second.second, first.second, eglContext)
                            EGL14.eglSwapBuffers(eglDisplay, first.second)
                        } else {
                            EGL14.eglSwapBuffers(eglDisplay, first.second)
                        }*/
        }
    }

    /**
     * 将代码送到gl线程执行
     * @param runnable action
     */
    fun post(runnable: Runnable) {
        if (Thread.currentThread() == eglThread) {
            runnable.run()
        } else {
            handler.post(runnable)
        }
    }
}
