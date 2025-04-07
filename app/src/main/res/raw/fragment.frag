#version 300 es
#extension GL_OES_EGL_image_external : require
precision mediump float;

varying vec2 textureCoordinate;
uniform sampler2D u_texture;
// uniform samplerExternalOES u_texture;
uniform vec4 v_color;
void main() {
    gl_FragColor = texture2D(u_texture, textureCoordinate);
}