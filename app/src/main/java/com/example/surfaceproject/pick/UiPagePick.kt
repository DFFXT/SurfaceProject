package com.example.surfaceproject.pick

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.example.surfaceproject.R
import com.fxf.debugwindowlibaray.ui.UIPage

class UiPagePick : UIPage() {
    override fun getTabIcon(): Int = com.fxf.debugwindowlibaray.R.mipmap.view_debug_common_close

    override fun onCreateContentView(ctx: Context): View {
        return CanvasView(ctx).apply {
            this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}
