package com.example.surfaceproject.gl.graph.model

import android.opengl.GLES20
import com.example.surfaceproject.gl.glsl.Loader
import com.example.surfaceproject.gl.util.toNativeBuffer

class Triangle(private val loader: Loader) {
    private val triangleCoords = floatArrayOf(-1f, 1f, 0f,
        1f, 1f, 0f,
        -1f, -1f, 0f,
        1f, -1f, 0f,)
    private var mPositionHandle = 0
    private var mColorHandle = 0
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    fun draw() {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(loader.program)

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(loader.program, "a_position")

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            triangleCoords.toNativeBuffer(),
        )

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(loader.program, "v_color")

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    companion object {
        // 绘制形状的顶点数量
        private const val COORDS_PER_VERTEX = 3
    }
}
