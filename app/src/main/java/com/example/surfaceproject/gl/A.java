package com.example.surfaceproject.gl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.surfaceproject.App;
import com.example.surfaceproject.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class A {
    public static void run() {
        // 1. 加载Bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(App.Companion.getCtx().getResources(), R.mipmap.icon_bg);

// 2. 生成纹理
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        int textureId = textureIds[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

// 将Bitmap上传到纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

// 3. 定义顶点和纹理坐标
        float[] vertexCoords = {
                -1.0f, -1.0f,  // 左下角
                0.50f, -1.0f,   // 右下角
                -1.0f, 1.0f,   // 左上角
                1.0f, 0.50f     // 右上角
        };

        float[] textureCoords = {
                0.0f, 1.0f,  // 左下角
                1.0f, 1.0f,  // 右下角
                0.0f, 0.0f,  // 左上角
                1.0f, 0.0f   // 右上角
        };

// 4. 创建顶点和纹理坐标缓冲区
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexCoords);
        vertexBuffer.position(0);

        FloatBuffer textureBuffer = ByteBuffer.allocateDirect(textureCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureCoords);
        textureBuffer.position(0);

// 5. 编写着色器
        String vertexShaderCode =
                "attribute vec4 aPosition;" +
                        "attribute vec2 aTexCoord;" +
                        "varying vec2 vTexCoord;" +
                        "void main() {" +
                        "  gl_Position = aPosition;" +
                        "  vTexCoord = aTexCoord;" +
                        "}";

        String fragmentShaderCode =
                "precision mediump float;" +
                        "varying vec2 vTexCoord;" +
                        "uniform sampler2D uTexture;" +
                        "void main() {" +
                        "  gl_FragColor = texture2D(uTexture, vTexCoord);" +
                        "}";

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

// 6. 获取属性位置
        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        int texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
        int textureHandle = GLES20.glGetUniformLocation(program, "uTexture");

// 7. 渲染
        GLES20.glUseProgram(program);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);

    }
    public static int loadShader(int type, String shaderCode) {
        // 创建着色器
        int shader = GLES20.glCreateShader(type);
        // 加载着色器代码
        GLES20.glShaderSource(shader, shaderCode);
        // 编译着色器
        GLES20.glCompileShader(shader);

        // 检查编译状态
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            // 如果编译失败，打印日志并删除着色器
            String log = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Shader compilation failed: " + log);
        }

        return shader;
    }

}
