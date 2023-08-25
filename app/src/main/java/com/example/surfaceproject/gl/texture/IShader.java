package com.example.surfaceproject.gl.texture;

public interface IShader {

    void onChange(int width, int height);

    void onDraw(int textureId, float[] texMatrix, int x, int y, int width, int height);

    void onDraw(int textureId, float[] verMatrix, float[] texMatrix, int x, int y, int width, int height);

    void onDestroy();
}
