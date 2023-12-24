package com.example.surfaceproject.pick.page

import android.content.Context
import android.graphics.RectF
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.example.surfaceproject.databinding.LayoutPickPageBinding
import com.example.surfaceproject.pick.ScreenRecordManager
import com.example.surfaceproject.media.record.ScreenCaptureCore
import com.fxf.debugwindowlibaray.ui.UIPage
import com.fxf.debugwindowlibaray.util.enableSelect

class UiPagePick(private val core: ScreenCaptureCore) : UIPage() {
    private lateinit var binding: LayoutPickPageBinding
    private val positionOnScreen by lazy {
        val arr = intArrayOf(0, 0)
        binding.root.getLocationOnScreen(arr)
        arr
    }
    private var screenRecordManager = ScreenRecordManager()
    override fun getTabIcon(): Int = com.fxf.debugwindowlibaray.R.mipmap.view_debug_common_close

    override fun onCreateContentView(ctx: Context, parent: ViewGroup): View {
        binding = LayoutPickPageBinding.inflate(LayoutInflater.from(ctx), parent, false)

        binding.canvasView.locationChangeListener = { rect ->

            binding.layoutToolBar.isVisible = rect.width() > 20
            binding.layoutToolBar.translationX = rect.left - positionOnScreen[0]
            binding.layoutToolBar.translationY = rect.bottom - positionOnScreen[1]

            if (rect.width() > 20) {
                if (screenRecordManager.prepare(rect)) {
                    binding.viewRect.updateLayoutParams<ViewGroup.LayoutParams> {
                        width = rect.width().toInt()
                        height = rect.height().toInt()
                    }
                } else {
                    Toast.makeText(ctx, "视频长宽太极端，无法输出视频", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.tvRecord.enableSelect()
        binding.tvRecord.setOnClickListener {
            binding.tvRecord.isSelected = !binding.tvRecord.isSelected
            // 开始录制时不允许修改大小
            binding.canvasView.enableResize(!binding.tvRecord.isSelected)

            if (binding.tvRecord.isSelected) {
                screenRecordManager.prepare(binding.canvasView.getCurrentRect())
                screenRecordManager.startRecord()
            } else {
                screenRecordManager.stop()
            }
        }

        binding.viewRect.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceCreated(holder: SurfaceHolder) {
                screenRecordManager.setPreviewSurface(holder.surface)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                screenRecordManager.removePreviewSurface(surface = holder.surface)
            }

            override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
            }
        })

        return binding.root
    }

    override fun onShow() {
        super.onShow()
        screenRecordManager.init(core, ctx)
    }

    interface PickListener {
        fun onResult(rectF: RectF) {}

        fun startRecord(rectF: RectF) {}
    }
}
