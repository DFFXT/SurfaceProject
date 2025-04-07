package com.example.surfaceproject.gl.graph.texture

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.example.surfaceproject.gl.glsl.Loader
import com.example.surfaceproject.gl.glsl.RectOESLoader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

// android 纹理坐标原点是左上角
// OpenGL 坐标原点是视图正中心

class OESTexture(loader: Loader) : BaseTexture(createOesTextureId(), loader) {

}
