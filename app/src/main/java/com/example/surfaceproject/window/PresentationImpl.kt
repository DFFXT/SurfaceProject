package com.example.surfaceproject.window

import android.content.Context
import android.content.DialogInterface
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

/**
 * 虚拟的Presentation，通过构造副屏Context，获取WindowManager来在副屏上添加View
 * 和真实的Presentation方法大概一致，可直接替换
 * 原因是直接使用Presentation无法投屏到副屏，模拟器和手机没问题，就是车机不行
 * 此方法为讯飞给出的解决方案
 * @author qinf
 * @date 2023/4/27
 */
class PresentationImpl(ctx: Context, private val display: Display) : DialogInterface {
    val context: Context = ctx.createDisplayContext(display)
    private val displayWindowManager = context.getSystemService(WindowManager::class.java)
    private lateinit var rootView: View
    private var onShowListener: DialogInterface.OnShowListener? = null
    private var onDismissListener: DialogInterface.OnDismissListener? = null
    private val maxSize: Point by lazy {
        val size = Point()
        display.getRealSize(size)
        size
    }

    // 默认显示区域
    val bound = Rect(0, 0, maxSize.x, maxSize.y)

    // 是否已经显示
    var isShow = false
        private set

    fun setContentView(@LayoutRes layout: Int) {
        rootView = LayoutInflater.from(context).inflate(layout, null, false)
    }

    fun <T> findViewById(@IdRes id: Int): T {
        return rootView.findViewById(id)
    }

    /**
     * 释放运行触摸
     */
    fun show(touchable: Boolean = true) {
        // LogUtil.d("PresentationImpl", "show $touchable $isShow")
        if (isShow) return
        isShow = true
        val mLayoutParams = WindowManager.LayoutParams()
        // 使用TYPE_STATUS_BAR_SUB_PANEL的值, 必须是车机才行否则没有权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && false) {
            mLayoutParams.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 17
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            // todo 在Android7上需要运行时权限，目前先不管
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        mLayoutParams.width = bound.width()
        mLayoutParams.height = bound.height()
        mLayoutParams.x = bound.left
        mLayoutParams.y = bound.top
        mLayoutParams.gravity = Gravity.START or Gravity.TOP
        mLayoutParams.flags = 40 or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        if (!touchable) {
            mLayoutParams.flags = mLayoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
        mLayoutParams.format = PixelFormat.TRANSLUCENT
        displayWindowManager.addView(rootView, mLayoutParams)
        this.onShowListener?.onShow(this)
    }

    override fun cancel() {
        // 暂不实现
    }

    override fun dismiss() {
        //LogUtil.d("PresentationImpl", "dismiss")
        isShow = false
        displayWindowManager.removeView(rootView)
        this.onDismissListener?.onDismiss(this)
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener) {
        this.onDismissListener = listener
    }

    fun setOnShowListener(listener: DialogInterface.OnShowListener) {
        this.onShowListener = listener
    }
}