package com.example.surfaceproject.pick.storage

import com.fxffxt.preferen.Config
import com.fxffxt.preferen.nullable

object VideoConfig : Config {
    override val localFileName: String = "video-config"

    var videoList: List<VideoItem> by nullable(key = "videoList", def = ArrayList())

    fun addVideoItem(item: VideoItem) {
        videoList = ArrayList(videoList).apply {
            add(item)
        }
    }

    class VideoItem(val name: String)
}
