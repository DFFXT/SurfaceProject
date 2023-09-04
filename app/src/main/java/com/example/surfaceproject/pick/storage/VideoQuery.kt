package com.example.surfaceproject.pick.storage

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.surfaceproject.App
import java.io.File
import java.util.LinkedList

/**
 * MediaStore方式访问video
 */
object VideoQuery {
    /**
     * 创建一个视频媒体文件
     */
    fun createVideo(name: String): Uri {
        val cv = ContentValues()
        cv.put(MediaStore.Video.Media.DISPLAY_NAME, name)
        cv.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cv.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        } else {
            // 低版本必须设置data字段，否则会随机生成data字段，而且和displayName字段不相同
            val dstPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_MOVIES + File.separator + name
            // DATA字段在Android 10.0 之后已经废弃
            cv.put(MediaStore.Images.ImageColumns.DATA, dstPath)
        }
        return App.ctx.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cv)!!
    }

    /**
     * 根据传入的displayName
     * 验证文件是否存在，返回存在项
     */
    fun getValidVideo(enumArgs: List<String>): List<Pair<String, Int>> {
        val result = LinkedList<Pair<String, Int>>()
        App.ctx.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME),
            " ${MediaStore.Video.Media.MIME_TYPE}=? and ${MediaStore.Video.Media.DISPLAY_NAME} in (\"${enumArgs.joinToString(",")}\")",
            arrayOf("video/mp4"),
            null,
        )?.use {
            while (it.moveToNext()) {
                result.add(Pair(it.getString(1), it.getInt(0)))
            }
        }
        return result
    }
}
