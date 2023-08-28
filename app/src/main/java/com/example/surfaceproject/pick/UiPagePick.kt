package com.example.surfaceproject.pick

import android.content.Context
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import com.example.surfaceproject.R
import com.fxf.debugwindowlibaray.ui.UIPage

class UiPagePick : UIPage() {
    private val resultListener: HashSet<PickListener> = HashSet()
    override fun getTabIcon(): Int = com.fxf.debugwindowlibaray.R.mipmap.view_debug_common_close

    override fun onCreateContentView(ctx: Context): View {
        return CanvasView(ctx).apply {
            this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            this.locationChangeListener = { rect ->
                ArrayList(resultListener).forEach {
                    it.onResult(rect)
                }
            }
        }
    }

    fun addPickResultListener(listener: PickListener) {
        resultListener.add(listener)
    }

    interface PickListener {
        fun onResult(rectF: RectF)
    }
}
