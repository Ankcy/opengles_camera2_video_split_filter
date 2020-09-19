//原始
#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 vTexCoord;

uniform samplerExternalOES sTexture;

void main() {
    vec4 mask = texture2D(sTexture, vTexCoord);//把sTexture上的一个点vTexCoord画到opengl上
    gl_FragColor = vec4(mask.rgb, 1.0);
}