package com.example.surfaceproject.gl.graph.texture;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;


import com.example.surfaceproject.R;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BaseFilter implements IShader {

    protected ShortBuffer mIndexSB;
    protected FloatBuffer mTexFB;
    protected FloatBuffer mPosFB;
    protected static final short[] VERTEX_INDEX = {
            0, 1, 3,
            2, 3, 1
    };
    protected static final float[] VERTEX_POS = {
            -1, 1.0f, 0f,
            -1, -1.0f, 0f,
            1, -1.0f, 0f,
            1, 1.0f, 0f,
    };
    public static final float[] TEX_VERTEX = {
            0, 1,
            0, 0,
            1, 0,
            1, 1,
    };

    protected int mProgram;
    protected int mPosition;
    protected int mTextureCoordinate;
    protected int mImageOESTexture;
    protected int uMvpMatrix;
    protected int uTexMatrix;
    protected float[] mMvpMatrix = new float[16];
    protected Context mContext;

    public BaseFilter(Context context, int resFrgId) {
        this.mContext = context;
        mIndexSB = ShaderHelper.arrayToShortBuffer(VERTEX_INDEX);
        mPosFB = ShaderHelper.arrayToFloatBuffer(VERTEX_POS);
        mTexFB = ShaderHelper.arrayToFloatBuffer(TEX_VERTEX);
        mProgram = ShaderHelper.loadProgram(context, R.raw.process_ver, resFrgId);
        mPosition = GLES20.glGetAttribLocation(mProgram, "inputPosition");
        mTextureCoordinate = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mImageOESTexture = GLES20.glGetUniformLocation(mProgram, "inputImageOESTexture");
        uMvpMatrix = GLES20.glGetUniformLocation(mProgram, "inputMatrix");
        uTexMatrix = GLES20.glGetUniformLocation(mProgram, "uTexMatrix");
        Matrix.setIdentityM(mMvpMatrix, 0);
    }

    @Override
    public void onChange(int width, int height) {
    }

    @Override
    public void onDraw(int textureId, float[] texMatrix, int x, int y, int width, int height) {
        onDraw(textureId, mMvpMatrix, texMatrix, x, y, width, height);
    }

    @Override
    public void onDraw(int textureId, float[] verMatrix, float[] texMatrix, int x, int y, int width, int height) {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPosition);
        GLES20.glVertexAttribPointer(mPosition, 3,
                GLES20.GL_FLOAT, false, 0, mPosFB);
        GLES20.glEnableVertexAttribArray(mTextureCoordinate);
        GLES20.glVertexAttribPointer(mTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 0, mTexFB);
        //GLES20.glUniformMatrix4fv(uMvpMatrix, 1, false, verMatrix, 0);
        //GLES20.glUniformMatrix4fv(uTexMatrix, 1, false, texMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mImageOESTexture, 0);
       // GLES20.glViewport(x, y, width, height);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        /*GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                GLES20.GL_UNSIGNED_SHORT, mIndexSB);*/
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glDisableVertexAttribArray(mPosition);
        GLES20.glDisableVertexAttribArray(mTextureCoordinate);
        GLES20.glUseProgram(0);
    }

    @Override
    public void onDestroy() {
        GLES20.glDeleteProgram(mProgram);
    }

}
