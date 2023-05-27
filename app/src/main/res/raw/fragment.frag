#version 300 es
precision mediump float;

varying vec2 v_textureCoordinate;
uniform sampler2D u_texture;
uniform vec4 v_color;
void main() {
    gl_FragColor = texture2D(u_texture, v_textureCoordinate);
}