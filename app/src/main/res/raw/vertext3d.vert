//#version 100 es
// 如果使用的是ES20或者ES10，则不要写#version 100 es，如果是ES30则需要写#version 300 es；而且attribute可以替换为layout(position = 0) in 的方式
attribute vec4 inputPosition;
attribute vec2 inputTextureCoordinate;
varying vec2 textureCoordinate;
void main() {
    textureCoordinate = inputTextureCoordinate;
    gl_Position = inputPosition;
}

