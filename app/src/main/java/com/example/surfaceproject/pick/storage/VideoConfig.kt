package com.example.surfaceproject.pick.storage

import com.fxffxt.preferen.Config
import com.fxffxt.preferen.nullable

object VideoConfig : Config {
    override val localFileName: String = "video-config"

    var videoList: ArrayList<VideoItem> by nullable(key = "videoList", def = ArrayList())

    class VideoItem(val path: String)
}
