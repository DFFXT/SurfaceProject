package com.example.surfaceproject.pick.page

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.surfaceproject.R
import com.example.surfaceproject.databinding.ItemVideoBinding
import com.example.surfaceproject.databinding.LayoutVideoListBinding
import com.example.surfaceproject.pick.storage.VideoConfig
import com.fxf.debugwindowlibaray.ui.UIPage

/**
 * 视频列表
 */
class UiPageVideo : UIPage() {
    private lateinit var binding: LayoutVideoListBinding
    private lateinit var videoList: List<VideoConfig.VideoItem>
    override fun onCreateContentView(ctx: Context, parent: ViewGroup): View {
        binding = LayoutVideoListBinding.inflate(LayoutInflater.from(ctx), parent, false)
        binding.rvList.adapter = object : RecyclerView.Adapter<VH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                return VH(ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                holder.binding.tvTitle.text = position.toString()
            }

            override fun getItemCount(): Int {
                return videoList.size
            }
        }
        return binding.root
    }

    override fun getTabIcon(): Int = R.mipmap.icon_tab_video_list


    override fun onShow() {
        super.onShow()
        VideoConfig.videoList = VideoConfig.videoList.apply {
            add(VideoConfig.VideoItem("sfdsfsd"))
        }
        videoList = VideoConfig.videoList
        binding.rvList.adapter?.notifyDataSetChanged()
    }

    private class VH(val binding: ItemVideoBinding) : ViewHolder(binding.root)
}
